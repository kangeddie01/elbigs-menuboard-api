package com.elbigs.service;

import com.elbigs.dto.AdverDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.AdverEntity;
import com.elbigs.entity.FileEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.AdverMapper;
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
public class AdverService {

    @Autowired
    private AdverMapper adverMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-adver}")
    String connectableTypeAdver;

    public List<AdverDto> selectAdverList(ShopReqDto req) {
        return adverMapper.selectAdverList(req);
    }

    public AdverDto selectAdver(long id) {

        AdverDto adverDto = adverMapper.selectAdver(id);

        if (adverDto != null) {
            adverDto.setFiles(commonMapper.selectFileList(connectableTypeAdver, id));
        }

        return adverDto;
    }

    private ResponsDto validateAdver(AdverEntity adverEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(adverEntity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }
        if (adverEntity.getStartDate() == null) {
            res.addErrors("start_date", new String[]{requireMsg});
        }
        if (adverEntity.getEndDate() == null) {
            res.addErrors("end_date", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateAdver(HttpServletRequest request, long adverId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = adverId >= 0 ? false : true;
        String[] tels = new String[3];

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();
        boolean isAll = false;
        boolean isReserved = false;

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
                System.out.println("key : " + k + ", val : " + v);
                if ("is_all".equals(k)) {
                    isAll = "1".equals(v) ? true : false;
                }
                if ("is_reserved".equals(k)) {
                    isReserved = "1".equals(v) ? true : false;
                }
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
        AdverEntity adverEntity = gson.fromJson(jsonElement, AdverEntity.class);

        adverEntity.setReserved(isReserved);
        adverEntity.setAll(isAll);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateAdver(adverEntity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        adverEntity.setId(adverId);
        adverEntity.setUserId(userPk);

        /* adverEntity save */
        if (!isNew) {
            adverMapper.updateAdver(adverEntity);
        } else {
            adverMapper.insertAdver(adverEntity);
            adverId = adverEntity.getId();
        }

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile[] files = new MultipartFile[5];

        files[0] = multipartHttpServletRequest.getFile("files[0]");
        files[1] = multipartHttpServletRequest.getFile("files[1]");
        files[2] = multipartHttpServletRequest.getFile("files[2]");
        files[3] = multipartHttpServletRequest.getFile("files[3]");
        files[4] = multipartHttpServletRequest.getFile("files[4]");

        /* file save */
        for (String f : delFiles) {
            commonMapper.deleteFiles(connectableTypeAdver, adverId, fileMap.get(f));
        }

        for (String f : changeFiles) {

            long changeId = fileMap.get(f);
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
                        .connectableId(adverId)
                        .connectableType(connectableTypeAdver)
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

    public void deleteAdver(long id) {
        adverMapper.deleteAdver(id);
    }

    public List<HashMap> selectAdListByKiosk(String macAddress, long userPk) {
        Map<String, Object> param = new HashMap<>();
        param.put("userPk", userPk);
        param.put("macAddress", macAddress);
        return adverMapper.selectAdListByKiosk(param);
    }
}

