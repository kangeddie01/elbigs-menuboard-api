package com.elbigs.controller.cms;

import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.service.UserShopService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/v1/cms")
public class ShopController {

    @Autowired
    private UserShopService userShopService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/shops")
    public ResponsDto shopList(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "category_id", required = false, defaultValue = "0") long categoryId
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setCategoryId(categoryId);

        if (page > 0) {
            req.setPage(page);
        }

        List<ShopDto> shops = userShopService.selectUserShopList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (shops != null && shops.size() > 0) {

            totalCount = shops.get(0).getShopTotalCount();

            if (totalCount > req.getPage() * req.getLength()) {
                int nextPageNo = req.getPage() + 1;
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/shops?page=" + nextPageNo;
            }

        }
        res.put("shopTotalCount", totalCount);
        res.put("next_url", nextUrl);
        res.put("shop_list", shops);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/shops/{shopId}")
    public ResponsDto shopInfo(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "shopId") long shopId) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);

        ShopDto shop = userShopService.selectShop(shopId);
        ResponsDto res = new ResponsDto();
        res.put("shop", shop);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/users/{userPk}/shops/{shopId}")
    public ResponsDto delete(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "shopId") long shopId) {

        userShopService.deleteShop(shopId);

        return new ResponsDto();
    }

    @PostMapping("/users/{userPk}/shops/{shopId}")
    public ResponsDto updateShop(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "shopId") long shopId) {

        ResponsDto res = userShopService.updateShop(request, shopId, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }

    @PostMapping("/users/{userPk}/shops")
    public ResponsDto insertShop(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk) {

        ResponsDto res = userShopService.updateShop(request, -1, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }


}
