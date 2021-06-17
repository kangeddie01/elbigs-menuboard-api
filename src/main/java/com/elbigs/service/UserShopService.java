package com.elbigs.service;

import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.MenuCategoryMapper;
import com.elbigs.mapper.UserShopMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.InitialSearchShopEntity;
import com.elbigs.entity.MenuCategoryShopEntity;
import com.elbigs.entity.ShopEntity;
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
public class UserShopService {

    @Autowired
    private UserShopMapper userShopMapper;
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

    @Value("${file.connectable-type-shop}")
    String connectableTypeShop;

    public List<ShopDto> selectUserShopList(ShopReqDto req) {
        return userShopMapper.selectUserShopList(req);
    }

    public ShopDto selectShop(long id) {

        ShopDto shopDto = userShopMapper.selectShop(id);

        if (shopDto != null) {
            shopDto.setFiles(commonMapper.selectFileList(connectableTypeShop, id));
            shopDto.setMenuCategoryIds(menuCategoryMapper.selectUserMenuCategoryShopList(id));
            shopDto.setInitialIds(commonMapper.selectInitialSearchShopList(id));
        }

        return shopDto;
    }

    public Map<String, Object> selectShopDetailFo(long id) {
        Map<String, Object> resultMap = new HashMap<>();


        List<FileEntity> files = commonMapper.selectFileList(connectableTypeShop, id);

        String[] langList = new String[]{"", "jp", "cn", "en"};

        for (String lang : langList) {
            Map<String, Object> shopMap = new HashMap<>();
            ArrayList<Map> tagsList = new ArrayList<>();
            List<String> fileList = new ArrayList<>();

            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            param.put("lang", "".equals(lang) ? "ko" : lang);
            HashMap<String, Object> shop = userShopMapper.selectShopDetailFo(param);

            shopMap.put("id", shop.get("id"));
            shopMap.put("contact", shop.get("contact"));
            shopMap.put("businessHours", shop.get("business_hours"));
            shopMap.put("name", shop.get("name"));
            shopMap.put("closedDay", shop.get("closed_day"));
            shopMap.put("availableService", shop.get("available_service"));
            String tags = (String) shop.get("tags");
            if (tags != null && tags.length() > 0) {

                for (String tag : tags.split(",")) {
                    Map<String, String> tagMap = new HashMap<>();
                    tagMap.put("name", tag);
                    tagsList.add(tagMap);
                }
                shopMap.put("tags", tagsList);

            }
            shopMap.put("isSmartOrder", shop.get("smart_order_yn"));
            shopMap.put("goods", new ArrayList<String>());

            if (files != null && files.size() > 0) {
                for (FileEntity f : files) {
                    fileList.add(f.getFullPath());
                }
                shopMap.put("images", fileList);
            } else {
                shopMap.put("images", new String[]{azureStorageUrl + "/store_noimg.png"});
            }
            resultMap.put("".equals(lang) ? "ko" : lang, shopMap);
            resultMap.put("visitTimeStatus", shop.get("visit_time_status"));
            resultMap.put("orderStatus", shop.get("order_status"));
        }
        return resultMap;
    }

    private ResponsDto validateShop(ShopEntity shopEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(shopEntity.getAddr())) {
            res.addErrors("addr", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(shopEntity.getAddrDetail())) {
            res.addErrors("addr_detail", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(shopEntity.getBusinessNumber())) {
            res.addErrors("business_number", new String[]{requireMsg});
        } else if (!ElbigsUtil.isValidBusinessNum(shopEntity.getBusinessNumber())) {
            res.addErrors("business_number"
                    , new String[]{messageSource.getMessage("error.msg.invalid-business-number"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(shopEntity.getName())) {
            res.addErrors("name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(shopEntity.getCnName())) {
            res.addErrors("cn_name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(shopEntity.getEnName())) {
            res.addErrors("en_name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(shopEntity.getJpName())) {
            res.addErrors("jp_name", new String[]{requireMsg});
        }
        if (!shopEntity.isAllTime() && !StringUtils.hasLength(shopEntity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!shopEntity.isAllTime() && !ElbigsUtil.isValidTime(shopEntity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!shopEntity.isAllTime() && !StringUtils.hasLength(shopEntity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!shopEntity.isAllTime() && !ElbigsUtil.isValidTime(shopEntity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }
        return res;
    }

    @Transactional
    public ResponsDto updateShop(HttpServletRequest request, long shopId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean newShop = shopId >= 0 ? false : true;
        boolean allTime = false;
        boolean smartOrderYn = false;
        String[] tels = new String[3];

        List<Long> categoryShopList = new ArrayList<>();
        List<Long> initialIds = new ArrayList<>();
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
                if ("is_smart_order".equals(k)) {
                    if ("1".equals(v)) {
                        smartOrderYn = true;
                    } else {
                        smartOrderYn = false;
                    }
                }
                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {
                    if (k.contains("menu_category_ids")) {
                        categoryShopList.add(Long.parseLong(v));
                    }
                    if (k.contains("initial_ids")) {
                        initialIds.add(Long.parseLong(v));
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
        ShopEntity shop = gson.fromJson(jsonElement, ShopEntity.class);

        shop.setAllTime(allTime);
        shop.setSmartOrderYn(smartOrderYn);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateShop(shop);
        System.out.println("=========================================================");
        System.out.println("locale : " + LocaleContextHolder.getLocale());

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(tels[0])) {
            validationRes.addErrors("tel_1", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone1(tels[0])) {
            validationRes.addErrors("tel_1"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_1"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[1])) {
            validationRes.addErrors("tel_2", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone2(tels[1])) {
            validationRes.addErrors("tel_2"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_2"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[2])) {
            validationRes.addErrors("tel_3", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone3(tels[2])) {
            validationRes.addErrors("tel_3"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_3"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (CollectionUtils.isEmpty(initialIds)) {
            validationRes.addErrors("initial_ids", new String[]{requedErrorMsg});
        }
        if (CollectionUtils.isEmpty(categoryShopList)) {
            validationRes.addErrors("menu_category_ids", new String[]{requedErrorMsg});
        }

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        if (tels[0] != null && tels[1] != null && tels[2] != null) {
            shop.setTel(tels[0] + "-" + tels[1] + "-" + tels[2]);
        }
        shop.setId(shopId);
        shop.setUserId(userPk);
        shop.setShopId("");//not null

        /* shop save */
        if (!newShop) {
            userShopMapper.updateShop(shop);
        } else {
            userShopMapper.insertShop(shop);
            shopId = shop.getId();
        }

        /* menu_category_shop save */
        if (!newShop) {
            menuCategoryMapper.deleteMenuCategoryShop(shopId);
        }
        for (Long categoryId : categoryShopList) {
            MenuCategoryShopEntity s = new MenuCategoryShopEntity();
            s.setShopId(shopId);
            s.setMenuCategoryId(categoryId);
            menuCategoryMapper.insertMenuCategoryShop(s);
        }

        /*initial_search_shop */
        if (!newShop) {
            commonMapper.deleteInitialSearchShop(shopId);
        }
        for (Long id : initialIds) {
            InitialSearchShopEntity s = new InitialSearchShopEntity();
            s.setShopId(shopId);
            s.setInitialSearchId(id);
            commonMapper.insertInitialSearchShop(s);
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
            commonMapper.deleteFiles(connectableTypeShop, shopId, fileMap.get(f));
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
                        .connectableId(shopId)
                        .connectableType(connectableTypeShop)
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

    public void deleteShop(long id) {
        userShopMapper.deleteShop(id);
    }
}

