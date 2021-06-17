package com.elbigs.controller.cms;

import com.elbigs.dto.EventDto;
import com.elbigs.dto.LineNoticeDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.service.LineNoticeService;
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
public class LineNoticeController {

    @Autowired
    private LineNoticeService lineNoticeService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/kiosk-notice")
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

        List<LineNoticeDto> list = lineNoticeService.selectLineNoticeList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/kiosk-notice?page=" + (req.getPage() + 1);
            }

        }
        res.put("next_url", nextUrl);
        res.put("notice_list", list);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/kiosk-notice/{noticeId}")
    public ResponsDto detail(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "noticeId") long noticeId) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);

        EventDto dto = lineNoticeService.selectLineNotice(noticeId);
        ResponsDto res = new ResponsDto();
        res.put("notice", dto);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/users/{userPk}/kiosk-notice/{noticeId}")
    public ResponsDto delete(@PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "noticeId") long noticeId) throws Exception {

        lineNoticeService.deleteLineNotice(noticeId);

        return new ResponsDto();
    }

    @PutMapping("/users/{userPk}/kiosk-notice/{noticeId}")
    public ResponsDto update(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "noticeId") long noticeId) {

        ResponsDto res = lineNoticeService.updateLineNotice(request, noticeId, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }

    @PostMapping("/users/{userPk}/kiosk-notice")
    public ResponsDto insert(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk) {

        ResponsDto res = lineNoticeService.updateLineNotice(request, -1, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }


}
