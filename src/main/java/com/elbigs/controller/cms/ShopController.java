package com.elbigs.controller.cms;

import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ResponseDto2;
import com.elbigs.dto.ShopDisaplyDto;
import com.elbigs.entity.UserEntity;
import com.elbigs.entity.menuboard.ShopDeviceEntity;
import com.elbigs.entity.menuboard.ShopDisplayEntity;
import com.elbigs.entity.menuboard.ShopEntity;
import com.elbigs.service.DisplayService;
import com.elbigs.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cms/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 매장 정보 저장
     *
     * @param entity
     * @return
     */
    @PostMapping("/")
    public ResponsDto saveShop(@RequestBody ShopEntity entity) {

        ResponsDto res = new ResponsDto();

        shopService.saveShop(entity);

        res.put("shopId", entity.getShopId());
        return res;
    }

    /**
     * 매장 디바이스 정보 저장
     *
     * @param entity
     * @return
     */
    @PostMapping("/device")
    public ResponseDto2 saveShopDevice(@RequestBody ShopDeviceEntity entity) {
        ResponseDto2<UserEntity> res = new ResponseDto2();
        return res;
    }

    /**
     * 매장 정보 조회
     *
     * @param shopId
     * @return
     */
    @GetMapping("/{shopId}")
    public ResponseDto2 selectShopDisplay(@PathVariable("shopId") long shopId) {
        ResponseDto2<ShopEntity> res = new ResponseDto2();
        res.setSuccess(true);
        res.setData(shopService.selectShop(shopId));
        return res;
    }

    /**
     * 매장 전치 리스트
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseDto2 selectShopList() {
        ResponseDto2<Iterable<ShopEntity>> res = new ResponseDto2();
        res.setSuccess(true);
        res.setData(shopService.selectShopList());
        return res;
    }
}
