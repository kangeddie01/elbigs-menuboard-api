package com.elbigs.controller.cms;

import com.elbigs.dto.MediaLibDto;
import com.elbigs.dto.ResponseDto;
import com.elbigs.dto.ResponseDto2;
import com.elbigs.entity.MediaCategoryEntity;
import com.elbigs.entity.MediaLibEntity;
import com.elbigs.service.DisplayService;
import com.elbigs.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cms")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MediaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MediaService mediaService;


    /**
     * 미디어 리스트 조회
     * @param shopId
     * @param mediaCategoryId -1 : 미분류만(카테고리 없는) , null or 0 : 전체
     * @param categoryType    bd :뱃지, bg : 배경화면
     * @param mediaType  i : 이미지, v : 동영상
     * @return
     */
    @GetMapping("/{shopId}/media-libs")
    public ResponseDto2 selectMediaLibList(@PathVariable("shopId") long shopId
            , @RequestParam(required = false, value = "mediaCategoryId") Long mediaCategoryId
            , @RequestParam(required = false, value = "categoryType") String categoryType
            , @RequestParam(required = false, value = "mediaType") String mediaType) {
        ResponseDto2<List<MediaLibDto>> res = new ResponseDto2();


        MediaLibEntity param = new MediaLibEntity();

        // 배지, 배경화면 일 경우
        if (categoryType != null && ("bd".equals(categoryType) || "bg".equals(categoryType))) {

            MediaCategoryEntity param1 = new MediaCategoryEntity();
            param1.setCategoryType(categoryType);
            param1.setShopId(shopId);
            List<MediaCategoryEntity> category = mediaService.selectMediaCategoryList(param1);

            if (category != null && category.size() > 0) {
                param.setMediaCategoryId(category.get(0).getMediaCategoryId());
            }

        } else {
            param.setMediaCategoryId(mediaCategoryId == null || mediaCategoryId == 0 ? null : mediaCategoryId);
        }

        param.setShopId(shopId);
        param.setMediaType(mediaType);

        List<MediaLibDto> list = mediaService.selectMediaLibList(param);
        res.setData(list);
        res.setSuccess(true);
        return res;
    }

    @PostMapping("/{shopId}/media-libs/upload")
    public ResponseDto2<MediaLibEntity> uploadFile(@PathVariable("shopId") long shopId
            , @RequestPart List<MultipartFile> file
            , @RequestParam("mediaType") String mediaType
            , @RequestParam(value = "mediaCategoryId", required = false) Long mediaCategoryId) {

        ResponseDto2<MediaLibEntity> res = new ResponseDto2();

        logger.info("param : mediaCategoryId : " + mediaCategoryId);
        logger.info("param : mediaType : " + mediaType);

        try {
            MediaLibEntity param = new MediaLibEntity();
            param.setMediaType(mediaType);
            param.setShopId(shopId);
            param.setMediaCategoryId(mediaCategoryId);
            MediaLibEntity newLib = mediaService.insertMediaLib(file.get(0), param);
            res.setData(newLib);
            res.setSuccess(true);
        } catch (Exception e) {
            res.setSuccess(false);
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            res.setErrors(error);
        }
        return res;

    }

    @GetMapping("/{shopId}/media-categorys")
    public ResponseDto2 selectMediaCategoryList(@PathVariable("shopId") long shopId,
                                                @RequestParam(value = "categoryType", required = false) String categoryType) {
        ResponseDto2<List<MediaCategoryEntity>> res = new ResponseDto2();

        MediaCategoryEntity param = new MediaCategoryEntity();
        param.setShopId(shopId);
        param.setCategoryType(categoryType);
        res.setData(mediaService.selectMediaCategoryList(param));
        res.setSuccess(true);
        return res;
    }



    /**
     * 미디어 카테고리 수정
     *
     * @param shopId
     * @param mediaCategoryId
     * @param entity
     * @return
     */
    @PutMapping("/{shopId}/media-categorys/{mediaCategoryId}")
    public ResponseDto updateMediaCategory(@PathVariable("shopId") long shopId
            , @PathVariable("mediaCategoryId") long mediaCategoryId
            , @RequestBody MediaCategoryEntity entity) {

        entity.setShopId(shopId);
        entity.setMediaCategoryId(mediaCategoryId);
        mediaService.updateMediaCategory(entity);

        ResponseDto res = new ResponseDto();
        res.setSuccess(true);
        return res;

    }

    /**
     * 미디어 카테고리 초기화 세팅
     *
     * <p>배경화면, 뱃지 카테고리 생성</p>
     *
     * @param shopId
     * @param entitys
     * @return
     */
    @PostMapping("/{shopId}/media-categorys/init")
    public ResponseDto initMediaCategory(@PathVariable("shopId") long shopId
            , @RequestBody List<MediaCategoryEntity> entitys) {

        mediaService.updateMediaCategoryAll(entitys);

        ResponseDto res = new ResponseDto();
        res.setSuccess(true);
        return res;

    }

    /**
     * 미디어 카테고리 신규 등록
     *
     * @param shopId
     * @param entity
     * @return
     */
    @PostMapping("/{shopId}/media-categorys")
    public ResponseDto createMediaCategory(@PathVariable("shopId") long shopId
            , @RequestBody MediaCategoryEntity entity) {

        entity.setShopId(shopId);
        mediaService.updateMediaCategory(entity);

        ResponseDto res = new ResponseDto();
        res.setSuccess(true);
        return res;

    }

    /**
     * 미디어 카테고리 삭제
     *
     * @param shopId
     * @param mediaCategoryId
     * @return
     */
    @DeleteMapping("/{shopId}/media-categorys/{mediaCategoryId}")
    public ResponseDto deleteMediaCategory(@PathVariable("shopId") long shopId
            , @PathVariable("mediaCategoryId") long mediaCategoryId) {

        mediaService.deleteMediaCategory(mediaCategoryId);

        ResponseDto res = new ResponseDto();
        res.setSuccess(true);
        return res;

    }

}
