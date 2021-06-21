package com.elbigs.controller.cms;

import com.elbigs.dto.ResponseDto2;
import com.elbigs.dto.ShopDisaplyDto;
import com.elbigs.entity.menuboard.ShopDeviceEntity;
import com.elbigs.entity.menuboard.ShopDisplayEntity;
import com.elbigs.service.DisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cms")
public class DisplayController {


    @Autowired
    private DisplayService displayService;

    /**
     * 전시 정보 등록
     *
     * @param dto
     * @return
     */
    @PostMapping("/{shopId}/test")
    public ResponseDto2 test(@RequestBody ShopDisaplyDto dto, @PathVariable("shopId") long shopId) {
        ResponseDto2<String> res = new ResponseDto2();
        dto.setShopId(shopId);
        displayService.saveContent(dto);
        return res;
    }

    /**
     * 전시 정보 등록
     *
     * @param dto
     * @return
     */
    @PostMapping("/{shopId}/shop-display")
    public ResponseDto2 saveContent(@RequestBody ShopDisaplyDto dto, @PathVariable("shopId") long shopId) {
        ResponseDto2<String> res = new ResponseDto2();
        dto.setShopId(shopId);
        displayService.saveContent(dto);
        return res;
    }

    /**
     * 전시 정보 수정
     *
     * @param dto
     * @return
     */
    @PutMapping("/{shopId}/shop-displays/{shopDisplayId}")
    public ResponseDto2 saveContent(@RequestBody ShopDisaplyDto dto
            , @PathVariable("shopId") long shopId
            , @PathVariable("shopDisplayId") long shopDisplayId) {
        ResponseDto2<String> res = new ResponseDto2();
        dto.setShopId(shopId);
        dto.setShopDisplayId(shopDisplayId);
        displayService.saveContent(dto);
        return res;
    }

    /**
     * 디바이스 -
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
     * 전시 상세조회
     *
     * @param shopDisplayId
     * @return
     */
    @GetMapping("/{shopDisplayId}")
    public ResponseDto2 selectShopDisplay(@PathVariable("shopDisplayId") long shopDisplayId) {
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
    @GetMapping("/{shopId}/list")
    public ResponseDto2 selectShopDisplayList(@PathVariable("shopId") long shopId) {
        ResponseDto2<List<ShopDisplayEntity>> res = new ResponseDto2();
        res.setData(displayService.selectShopDisplayList(shopId));
        return res;
    }
}
