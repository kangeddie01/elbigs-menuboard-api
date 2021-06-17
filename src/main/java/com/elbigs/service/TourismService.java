package com.elbigs.service;

import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.dto.TourismDto;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.TourismEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.TourismMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class TourismService {

    @Autowired
    private TourismMapper tourismMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-tourism}")
    String connectableTypeTourism;

    public List<TourismDto> selectTourismList(ShopReqDto req) {
        return tourismMapper.selectTourismList(req);
    }

    public Map<String, Object> selectTourismListFo(ShopReqDto req) {
        List<String> langList = new ArrayList<>();
        langList.add("ko");
        langList.add("jp");
        langList.add("cn");
        langList.add("en");
        List<String> banners = null;

        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> tourMapList = new ArrayList<>();
        Map<String, Object> tourMap = new HashMap<>();

        Map<Long, List> bannerMap = new HashMap<>();
        Map<Long, String> qrMap = new HashMap<>();

        for (String lang : langList) {
            req.setLang(lang);
            List<TourismDto> tourList = tourismMapper.selectTourismListByLang(req);

            if (tourList == null || tourList.size()==0) {
                return null;
            }

            if ("ko".equals(lang)) {
                for (TourismDto dto : tourList) {
                    List<FileEntity> files = commonMapper.selectFileList(connectableTypeTourism, dto.getId());
                    if (files != null) {
                        banners = new ArrayList<>();
                        for (FileEntity f : files) {
                            if (f.getFileNumber() == 0) {
                                qrMap.put(dto.getId(), f.getFullPath());
                            } else {
                                banners.add(f.getFullPath());
                            }
                        }
                        bannerMap.put(dto.getId(), banners);
                    }
                }
            }

            for (TourismDto dto : tourList) {
                tourMap.put("address", dto.getAddr());
                tourMap.put("banners", bannerMap.get(dto.getId()));
                tourMap.put("closed", dto.getHoliday());
                tourMap.put("contact", dto.getInformation());
                tourMap.put("facility", "");
                tourMap.put("fee", dto.getCharges());
                tourMap.put("id", dto.getId());
                tourMap.put("name", dto.getName());
                tourMap.put("qrCode", qrMap.get(dto.getId()));
                tourMap.put("time", dto.getTime());
                tourMapList.add(tourMap);
                tourMap = new HashMap<>();
            }

            if (tourMapList.size() > 0) {
                resultMap.put(lang, tourMapList);
            }
            tourMapList = new ArrayList<>();
        }

        return resultMap;
    }

    public TourismDto selectTourism(long id) {

        TourismDto tourismDto = tourismMapper.selectTourism(id);

        if (tourismDto != null) {

            List<FileEntity> files = commonMapper.selectFileList(connectableTypeTourism, id);
            List<FileEntity> fileList = new ArrayList<>();

            for (FileEntity file : files) {
                if (file.getFileNumber() == 0) {
                    List<Map<String, String>> qrList = new ArrayList<>();
                    Map<String, String> qr = new HashMap<>();
                    qr.put("origin_name", file.getOriginName());
                    qrList.add(qr);
                    tourismDto.setQrImg(qrList);
                } else {
                    fileList.add(file);
                }
            }

            tourismDto.setFiles(fileList);
        }

        return tourismDto;
    }

    private ResponsDto validateTourism(TourismEntity tourismEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(tourismEntity.getAddr())) {
            res.addErrors("addr", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(tourismEntity.getName())) {
            res.addErrors("name", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateTourism(HttpServletRequest request, long tourismId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = tourismId >= 0 ? false : true;

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);
            System.out.println("key : " + k);

            for (String v : vals) {
                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {

                    if (k.contains("files")) {
                        if (k.contains("id")) {
                            fileMap.put(k.substring(0, 8), Long.parseLong(v));
                        }
                        if (v != null && v.contains("delete")) {
                            delFiles.add(k.substring(0, 8));
                        }
                        if ("change".equals(v)) {
                            changeFiles.add(k.substring(0, 8));
                        }

                    }
                }
            }
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonElement jsonElement = gson.toJsonTree(newMap);
        TourismEntity entity = gson.fromJson(jsonElement, TourismEntity.class);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateTourism(entity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!validationRes.isSuccess()) {
            return validationRes;
        }
        entity.setId(tourismId);
        entity.setUserId(userPk);

        /*  save */
        if (!isNew) {
            tourismMapper.updateTourism(entity);
        } else {
            tourismMapper.insertTourism(entity);
            tourismId = entity.getId();
        }

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile[] files = new MultipartFile[5];

        files[0] = multipartHttpServletRequest.getFile("files[0]");
        files[1] = multipartHttpServletRequest.getFile("files[1]");
        files[2] = multipartHttpServletRequest.getFile("files[2]");
        files[3] = multipartHttpServletRequest.getFile("files[3]");
        files[4] = multipartHttpServletRequest.getFile("files[4]");
        MultipartFile qrImg = multipartHttpServletRequest.getFile("qr_img");

        /* file save */
        for (String f : delFiles) {
            commonMapper.deleteFiles(connectableTypeTourism, tourismId, fileMap.get(f));
        }

        if (qrImg != null) {
            String dir = DateUtil.getCurrDateStr("yyyyMMdd");
            String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
            String ext = qrImg.getOriginalFilename().substring(qrImg.getOriginalFilename().length() - 3);
            String uploadPath = dir + "/" + convertName + "." + ext;

            System.out.println("====================================================================");
            String uploadedPath = azureBlobAdapter.upload(qrImg, uploadPath);

            if (uploadedPath != null) {
                String fullPath = azureStorageUrl + "/" + uploadedPath;

                FileEntity fileEntity = FileEntity.builder()
                        .fileNumber(0)
                        .connectableId(tourismId)
                        .connectableType(connectableTypeTourism)
                        .originName(qrImg.getOriginalFilename())
                        .convertName(convertName + "." + ext)
                        .fullPath(fullPath)
                        .type("file")
                        .mimeType(qrImg.getContentType())
                        .size(String.valueOf(qrImg.getSize()))
                        .build();
                commonMapper.deleteFiles(connectableTypeTourism, tourismId, 0);
                commonMapper.insertFiles(fileEntity);
            }
        }

        for (String f : changeFiles) {

            long changeId = fileMap.get(f) - 1;
            MultipartFile file = files[(int) changeId];
            String dir = DateUtil.getCurrDateStr("yyyyMMdd");
            String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 3);
            String uploadPath = dir + "/" + convertName + "." + ext;

            System.out.println("====================================================================");
            String uploadedPath = azureBlobAdapter.upload(file, uploadPath);

            if (uploadedPath != null) {
                String fullPath = azureStorageUrl + "/" + uploadedPath;

                FileEntity fileEntity = FileEntity.builder()
                        .fileNumber(fileMap.get(f))
                        .connectableId(tourismId)
                        .connectableType(connectableTypeTourism)
                        .originName(file.getOriginalFilename())
                        .convertName(convertName + "." + ext)
                        .fullPath(fullPath)
                        .type("file")
                        .mimeType(file.getContentType())
                        .size(String.valueOf(file.getSize()))
                        .build();

                commonMapper.insertFiles(fileEntity);
            }
        }
        validationRes.setSuccess(true);
        return validationRes;
    }

    public void deleteTourism(long id) {
        tourismMapper.deleteTourism(id);
    }
}

