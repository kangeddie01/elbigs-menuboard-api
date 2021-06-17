package com.elbigs.controller.cms;

import com.elbigs.dto.TourismDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.service.TourismService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/v1/cms")
public class TourismsController {

    @Autowired
    private TourismService tourismService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/tourism")
    public ResponsDto list(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "20") int length) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);

        if (page > 0) {
            req.setPage(page);
        }

        List<TourismDto> list = tourismService.selectTourismList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/tourism?page=" + (req.getPage() + 1);
            }

        }
        res.put("tourismTotalCount", totalCount);
        res.put("next_url", nextUrl);
        res.put("tourism_list", list);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/tourism/{tourismId}")
    public ResponsDto detail(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "tourismId") long tourismId) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);

        TourismDto dto = tourismService.selectTourism(tourismId);
        ResponsDto res = new ResponsDto();
        res.put("tourism", dto);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/users/{userPk}/tourism/{tourismId}")
    public ResponsDto delete(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "tourismId") long tourismId) throws Exception {

        tourismService.deleteTourism(tourismId);

        return new ResponsDto();
    }

    @PutMapping("/users/{userPk}/tourism/{tourismId}")
    public ResponsDto update(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "tourismId") long tourismId) {

        ResponsDto res = tourismService.updateTourism(request, tourismId, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }

    @PostMapping("/users/{userPk}/tourism")
    public ResponsDto insert(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk) {

        ResponsDto res = tourismService.updateTourism(request, -1, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }


}
