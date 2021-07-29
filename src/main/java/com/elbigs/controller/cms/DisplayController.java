package com.elbigs.controller.cms;

import com.elbigs.dto.*;
import com.elbigs.entity.*;
import com.elbigs.service.DisplayService;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DisplayController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DisplayService displayService;


    /**
     * 전시 정보 등록
     *
     * @param dto
     * @return
     */
    @PostMapping("/{shopId}/shop-display")
    public ResponseDto saveContent(@RequestBody ShopDisaplyDto dto, @PathVariable("shopId") long shopId) {
        ResponseDto res = new ResponseDto();

        if (!StringUtils.hasLength(dto.getDisplayHtml())) {
            res.setSuccess(false);
            return res;
        }
        dto.setShopId(shopId);

        try {
            ShopDisplayEntity shopDisplay = displayService.saveContent(dto);
            res.setSuccess(true);
            res.put("shopDisplay", shopDisplay);

        } catch (Exception e) {
            res.setSuccess(false);
            res.addErrors("ERROR_001", e.getMessage());
        }


        return res;
    }


    /**
     * 전시 정보 수정
     *
     * @param dto
     * @return
     */
    @PutMapping("/{shopId}/shop-displays/{shopDisplayId}/modify-title")
    public ResponseDto2 saveContent(@RequestBody ShopDisaplyDto dto
            , @PathVariable("shopId") long shopId
            , @PathVariable("shopDisplayId") long shopDisplayId) {
        ResponseDto2<String> res = new ResponseDto2();
        displayService.updateShopDisplayTitle(shopDisplayId, dto.getDisplayName());
        res.setSuccess(true);
        return res;
    }

    /**
     * 디바이스 저장
     *
     * @param dto
     * @param shopId
     * @return
     */
    @PutMapping("/{shopId}/shop-devices/{shopDeviceId}")
    public ResponseDto2 saveShopDeviceDisplay(@RequestBody ShopDeviceEntity dto
            , @PathVariable("shopId") long shopId
            , @PathVariable("shopDeviceId") long shopDeviceId) {
        ResponseDto2<String> res = new ResponseDto2();
        dto.setShopId(shopId);
        dto.setShopDeviceId(shopDeviceId);
        displayService.saveShopDeviceDisplay(dto);
        return res;
    }

    /**
     * panel-display mapping ( 단건 )
     *
     * @param dto
     * @param shopId
     * @return
     */
    @PutMapping("/{shopId}/panel-display-mapping")
    public ResponseDto2 updateDeviceDisplayMapping(@RequestBody ShopDeviceEntity dto
            , @PathVariable("shopId") long shopId) {
        ResponseDto2<String> res = new ResponseDto2();
        displayService.updateDeviceDisplayMapping(dto);
        res.setSuccess(true);
        return res;
    }

    /**
     * panel-display mapping ( list )
     *
     * @param dto
     * @param shopId
     * @return
     */
    @PutMapping("/{shopId}/panel-display-mappings")
    public ResponseDto2 updateDeviceDisplayMappingList(@RequestBody List<ShopDeviceEntity> mappings
            , @PathVariable("shopId") long shopId) {
        ResponseDto2<String> res = new ResponseDto2();
        displayService.updateDeviceDisplayMappings(mappings);
        res.setSuccess(true);
        return res;
    }

    /**
     * delete display
     *
     * @param shopId
     * @param shopDisplayId
     * @return
     */
    @DeleteMapping("/{shopId}/shop-displays/{shopDisplayId}")
    public ResponseDto2 deleteShopDisplay(@PathVariable("shopId") long shopId, @PathVariable("shopDisplayId") long shopDisplayId) {
        ResponseDto2<String> res = new ResponseDto2();
        displayService.deleteShopDisplay(shopDisplayId);
        res.setSuccess(true);
        return res;
    }


    /**
     * 전시 상세조회
     *
     * @param shopDisplayId
     * @return
     */
    @GetMapping("/{shopId}/shop-displays/{shopDisplayId}")
    public ResponseDto2 selectShopDisplay(@PathVariable("shopId") long shopId
            , @PathVariable("shopDisplayId") long shopDisplayId) {
        ResponseDto2<ShopDisplayEntity> res = new ResponseDto2();
        res.setData(displayService.selectShopDisplay(shopDisplayId));
        return res;
    }

    /**
     * 상점의 전시리스트 조회
     *
     * @param shopId
     * @return
     */
    @GetMapping("/{shopId}/shop-displays")
    public ResponseDto2 selectShopDisplayList(@PathVariable("shopId") long shopId,
                                              @RequestParam("screenRatio") String screenRatio) {
        ResponseDto2<List<ShopDisplayEntity>> res = new ResponseDto2();
        res.setData(displayService.selectShopDisplayList(shopId, screenRatio));
        return res;
    }

    /**
     * @param shopId
     * @return
     */
    @GetMapping("/{shopId}/shop-devices")
    public ResponseDto2 selectShopDeviceList(@PathVariable("shopId") long shopId) {
        ResponseDto2<List<ShopDeviceDto>> res = new ResponseDto2();
        res.setData(displayService.selectShopDeviceList(shopId));
        return res;
    }


//    @GetMapping("/template-categorys")
//    public ResponseDto2 selectTemplateCategoryList() {
//        ResponseDto2<List<TemplateCategoryEntity>> res = new ResponseDto2();
//        res.setData(displayService.selectTemplateCategoryList());
//        res.setSuccess(true);
//        return res;
//    }

    @GetMapping("/template-categorys")
    public ResponseDto2 selectTemplateCategoryList2(@RequestParam(required = true, value = "upperCategoryId") Long upperCategoryId) {
        ResponseDto2<List<TemplateCategoryEntity>> res = new ResponseDto2();
        res.setData(displayService.selectTemplateCategoryListByUpper(upperCategoryId));
        res.setSuccess(true);
        return res;
    }

    @GetMapping("/display-templates/recommend")
    public ResponseDto2 selectRecommendTEmplateList(
            @RequestParam(required = false, value = "templateCategoryId") Long templateCategoryId
    ) {
        ResponseDto2<List<HtmlTemplateEntity>> res = new ResponseDto2();
        res.setData(displayService.selectTemplateList2(templateCategoryId, null, "Y"));
        res.setSuccess(true);
        return res;
    }

    @GetMapping("/display-templates")
    public ResponseDto2 selectRecommendTEmplateList(
            @RequestParam(required = false, value = "templateCategoryId") Long templateCategoryId,
            @RequestParam(required = false, value = "screenRatio") String screenRatio,
            @RequestParam(required = false, value = "orderBy") String orderBy
    ) {
        ResponseDto2<List<HtmlTemplateEntity>> res = new ResponseDto2();
        res.setData(displayService.selectTemplateList2(templateCategoryId, screenRatio, "N"));
        res.setSuccess(true);
        return res;
    }

    @PutMapping("/{shopId}/shop-displays/{shopDisplayId}/copy")
    public ResponseDto copyDisplay(@PathVariable("shopId") long shopId
            , @PathVariable("shopDisplayId") long shopDisplayId) {

        ResponseDto res = new ResponseDto();
        try {
            displayService.copyDisplay(shopId, shopDisplayId);
            res.setSuccess(true);
        } catch (Exception e) {
            res.setSuccess(false);
            res.addErrors("ERROR_001", e.getMessage());
        }

        return res;
    }


}
