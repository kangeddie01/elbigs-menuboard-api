package com.elbigs.controller.cms;

import com.elbigs.dto.*;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.ShopEntity;
import com.elbigs.service.ShopService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/shops")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ShopController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShopService shopService;

    /**
     * 매장 정보 등록
     *
     * @param entity
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping
    public ResponseDto createShop(@RequestBody ShopEntity entity) {

        ResponseDto res = shopService.saveShop(entity);

        res.put("shopId", entity.getShopId());

        return res;
    }

    /**
     * 매장 정보 수정
     *
     * @param entity
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/{shopId}")
    public ResponseDto saveShop(@RequestBody ShopEntity entity, @PathVariable("shopId") long shopId) {

        ResponseDto res = new ResponseDto();
        entity.setShopId(shopId);
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/device")
    public ResponseDto2 saveShopDevice(@RequestBody ShopDeviceEntity entity) {
        ResponseDto2<String> res = new ResponseDto2();
        return res;
    }

    /**
     * 매장 정보 조회
     *
     * @param shopId
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/{shopId}")
    public ResponseDto2 selectShopDisplay(@PathVariable("shopId") long shopId) {

        ResponseDto2<ShopEntity> res = new ResponseDto2();
        res.setSuccess(true);
        res.setData(shopService.selectShop(shopId));
        return res;
    }

    /**
     * 매장 리스트
     *
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/list")
    public ListResponseDto selectShopList(PagingParam param) {

        List<ShopEntity> list = shopService.selectShopList(param);
        int totalCount = 0;

        if (list != null && list.size() > 0) {
            totalCount = list.get(0).getTotalCount();
        }

        return ListResponseDto.<ShopEntity>builder()
                .totalCount(totalCount)
                .page(param.getPage())
                .length(param.getLength())
                .list(list)
                .build();
    }
}
