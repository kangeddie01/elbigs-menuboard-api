package com.elbigs.controller.cms;

import com.elbigs.dto.FacilityDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.service.FacilityService;
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
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/facilities")
    public ResponsDto list(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "category_id", required = false, defaultValue = "0") long categoryId
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "20") int length) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setCategoryId(categoryId);

        if (page > 0) {
            req.setPage(page);
        }

        List<FacilityDto> list = facilityService.selectFacilityList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/facilities?page=" + (req.getPage() + 1);
            }

        }
        res.put("facilityTotalCount", totalCount);
        res.put("next_url", nextUrl);
        res.put("facilities_list", list);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/facilities/{facilityId}")
    public ResponsDto detail(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "facilityId") long facilityId) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);

        FacilityDto dto = facilityService.selectFacility(facilityId);
        ResponsDto res = new ResponsDto();
        res.put("facilities", dto);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/users/{userPk}/facilities/{facilityId}")
    public ResponsDto delete(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "facilityId") long facilityId) throws Exception {

        facilityService.deleteFacility(facilityId);

        return new ResponsDto();
    }

    @PostMapping("/users/{userPk}/facilities/{facilityId}")
    public ResponsDto update(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "facilityId") long facilityId) {

        ResponsDto res = facilityService.updateFacility(request, facilityId, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }

    @PostMapping("/users/{userPk}/facilities")
    public ResponsDto insert(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk) {

        ResponsDto res = facilityService.updateFacility(request, -1, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }


}
