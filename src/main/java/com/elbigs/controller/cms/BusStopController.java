package com.elbigs.controller.cms;

import com.elbigs.dto.BusStopDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.BusRouteEntity;
import com.elbigs.service.BusStopService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cms")
public class BusStopController {

    @Autowired
    private BusStopService busStopService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/stops")
    public ResponsDto busStopList(@PathVariable("userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setUserPk(userPk);

        if (page > 0) {
            req.setPage(page);
        }

        List<BusStopDto> list = busStopService.selectBusStopList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/stops?page=" + (req.getPage() + 1);
            }
        }
        res.put("next_url", nextUrl);
        res.put("busStops", list);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/stops/{busStopPk}/routes")
    public ResponsDto busRouteList(@PathVariable("userPk") long userPk, @PathVariable("busStopPk") long busStopPk) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setBusStopPk(busStopPk);

        List<BusRouteEntity> list = busStopService.selectBusStopRouteList(busStopPk);
        ResponsDto res = new ResponsDto();
        res.put("busRoutes", list);
        return res;
    }
}
