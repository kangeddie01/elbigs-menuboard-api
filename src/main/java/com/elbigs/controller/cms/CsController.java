package com.elbigs.controller.cms;

import com.elbigs.dto.*;
import com.elbigs.service.MessageService;
import com.elbigs.service.NoticeService;
import com.elbigs.service.QnaService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cms")
public class CsController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private QnaService qnaService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/notice")
    public ResponsDto list(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        if (page > 0) {
            req.setPage(page);
        }

        List<NoticeDto> list = noticeService.selectNoticeList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/notice?page=" + (req.getPage() + 1);
            }
        }
        res.put("next_url", nextUrl);
        res.put("notice_list", list);

        return res;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/message")
    public ResponsDto messageList(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        if (page > 0) {
            req.setPage(page);
        }

        List<MessageDto> list = messageService.selectMessageList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/message?page=" + (req.getPage() + 1);
            }
        }
        res.put("next_url", nextUrl);
        res.put("message_list", list);

        return res;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/qna")
    public ResponsDto qnaList(@PathVariable(name = "userPk") long userPk
            , @RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        if (page > 0) {
            req.setPage(page);
        }

        List<QnaDto> list = qnaService.selectQnaList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users/" + userPk + "/qna?page=" + (req.getPage() + 1);
            }
        }
        res.put("next_url", nextUrl);
        res.put("qna_list", list);

        return res;
    }

    @PostMapping("/users/{userPk}/qna")
    public ResponsDto insertQna(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "userPk") long userPk) {

        ResponsDto res = qnaService.updateQna(request, -1, userPk);

        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }

        return res;
    }

}
