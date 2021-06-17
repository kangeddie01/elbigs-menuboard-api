package com.elbigs.service;

import com.elbigs.dto.FacilityDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.FacilityMapper;
import com.elbigs.mapper.MenuCategoryMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.entity.FacilityEntity;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.MenuCategoryFacilityEntity;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class FacilityService {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private MenuCategoryMapper menuCategoryMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-facility}")
    String connectableTypeFacility;

    public List<FacilityDto> selectFacilityList(ShopReqDto req) {
        return facilityMapper.selectFacilityList(req);
    }

    public FacilityDto selectFacility(long id) {

        FacilityDto facilityDto = facilityMapper.selectFacility(id);

        if (facilityDto != null) {
            facilityDto.setFiles(commonMapper.selectFileList(connectableTypeFacility, id));
            facilityDto.setMenuCategoryIds(menuCategoryMapper.selectUserMenuCategoryFacilityList(id));
        }

        return facilityDto;
    }


    public Map<String, Object> selectFacilityDetailFo(long id) {
        Map<String, Object> resultMap = new HashMap<>();


        List<FileEntity> files = commonMapper.selectFileList(connectableTypeFacility, id);

        String[] langList = new String[]{"", "jp", "cn", "en"};

        for (String lang : langList) {
            Map<String, Object> shopMap = new HashMap<>();
            ArrayList<Map> tagsList = new ArrayList<>();
            List<String> fileList = new ArrayList<>();

            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            param.put("lang", "".equals(lang) ? "ko" : lang);
            HashMap<String, Object> facilityDetailFo = facilityMapper.selectFacilityDetailFo(param);

            shopMap.put("id", facilityDetailFo.get("id"));
            shopMap.put("businessHours", facilityDetailFo.get("business_hours"));
            shopMap.put("name", facilityDetailFo.get("name"));
            shopMap.put("closedDay", facilityDetailFo.get("closed_day"));
            shopMap.put("availableService", facilityDetailFo.get("available_service"));
            String tags = (String) facilityDetailFo.get("tags");
            if (tags != null && tags.length() > 0) {

                for (String tag : tags.split(",")) {
                    Map<String, String> tagMap = new HashMap<>();
                    tagMap.put("name", tag);
                    tagsList.add(tagMap);
                }
                shopMap.put("tags", tagsList);

            }

            if (files != null && files.size() > 0) {
                for (FileEntity f : files) {
                    fileList.add(f.getFullPath());
                }
                shopMap.put("images", fileList);
            } else {
                shopMap.put("images", new String[]{azureStorageUrl + "/store_noimg.png"});
            }
            resultMap.put("".equals(lang) ? "ko" : lang, shopMap);
        }
        return resultMap;
    }

    private ResponsDto validateFacility(FacilityEntity facilityEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(facilityEntity.getAddr())) {
            res.addErrors("addr", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(facilityEntity.getAddrDetail())) {
            res.addErrors("addr_detail", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(facilityEntity.getName())) {
            res.addErrors("name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(facilityEntity.getCnName())) {
            res.addErrors("cn_name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(facilityEntity.getEnName())) {
            res.addErrors("en_name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(facilityEntity.getJpName())) {
            res.addErrors("jp_name", new String[]{requireMsg});
        }

        if (!facilityEntity.isAllTime() && !StringUtils.hasLength(facilityEntity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!facilityEntity.isAllTime() && !ElbigsUtil.isValidTime(facilityEntity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!facilityEntity.isAllTime() && !StringUtils.hasLength(facilityEntity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!facilityEntity.isAllTime() && !ElbigsUtil.isValidTime(facilityEntity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateFacility(HttpServletRequest request, long facilityId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = facilityId >= 0 ? false : true;
        boolean allTime = false;
        String[] tels = new String[3];

        List<Long> categoryList = new ArrayList<>();
        Map<String, Object> newMap = new HashMap<>();

        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);
            System.out.println("key : " + k);
            System.out.println(vals);

            for (String v : vals) {
                if ("tel_1".equals(k)) {
                    tels[0] = v;
                }
                if ("tel_2".equals(k)) {
                    tels[1] = v;
                }
                if ("tel_3".equals(k)) {
                    tels[2] = v;
                }
                if ("all_time".equals(k)) {
                    if ("1".equals(v)) {
                        allTime = true;
                    } else {
                        allTime = false;
                    }
                }
                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {
                    if (k.contains("menu_category_ids")) {
                        categoryList.add(Long.parseLong(v));
                    }

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
        FacilityEntity facilityEntity = gson.fromJson(jsonElement, FacilityEntity.class);

        facilityEntity.setAllTime(allTime);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateFacility(facilityEntity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (StringUtils.hasLength(tels[0]) && !ElbigsUtil.isValidPhone1(tels[0])) {
            validationRes.addErrors("tel_1"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_1"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (StringUtils.hasLength(tels[1]) && !ElbigsUtil.isValidPhone2(tels[1])) {
            validationRes.addErrors("tel_2"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_2"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (StringUtils.hasLength(tels[2]) && !ElbigsUtil.isValidPhone3(tels[2])) {
            validationRes.addErrors("tel_3"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_3"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (CollectionUtils.isEmpty(categoryList)) {
            validationRes.addErrors("menu_category_ids", new String[]{requedErrorMsg});
        }

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        if (tels[0] != null && tels[1] != null && tels[2] != null) {
            facilityEntity.setTel(tels[0] + "-" + tels[1] + "-" + tels[2]);
        }
        facilityEntity.setId(facilityId);
        facilityEntity.setUserId(userPk);

        /* facilityEntity save */
        if (!isNew) {
            facilityMapper.updateFacility(facilityEntity);
        } else {
            facilityMapper.insertFacility(facilityEntity);
            facilityId = facilityEntity.getId();
        }

        /* menu_category_facility save */
        if (!isNew) {
            menuCategoryMapper.deleteMenuCategoryFacility(facilityId);
        }
        for (Long categoryId : categoryList) {
            MenuCategoryFacilityEntity s = new MenuCategoryFacilityEntity();
            s.setFacilityId(facilityId);
            s.setMenuCategoryId(categoryId);
            menuCategoryMapper.insertMenuCategoryFacility(s);
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
            commonMapper.deleteFiles(connectableTypeFacility, facilityId, fileMap.get(f));
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
                        .connectableId(facilityId)
                        .connectableType(connectableTypeFacility)
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

    public void deleteFacility(long id) {
        facilityMapper.deleteFacility(id);
    }
}

