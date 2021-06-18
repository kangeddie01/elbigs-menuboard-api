package com.elbigs.controller.cms;

import com.elbigs.dto.*;
import com.elbigs.entity.UserEntity;
import com.elbigs.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cms")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/header/users")
    public ResponsDto userList() {

        ResponsDto res = new ResponsDto();
        List<Map<String, Object>> userMapList = new ArrayList<>();

        List<UserEntity> users = userService.selectUserListAll(null);
        for (UserEntity user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMapList.add(userMap);
        }

        res.put("users", userMapList);
        return res;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users")
    public ResponsDto userList2(@RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "length", required = false, defaultValue = "0") int length
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) throws Exception {

        ShopReqDto req = new ShopReqDto();
        req.setSearchStr(searchStr);
        req.setLength(length);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        if (page > 0) {
            req.setPage(page);
        }

        List<UserDto> list = userService.selectUserList(req);
        ResponsDto res = new ResponsDto();
        String nextUrl = null;
        int totalCount = 0;

        if (list != null && list.size() > 0) {

            totalCount = list.get(0).getTotalCount();
            if (totalCount > req.getPage() * req.getLength()) {
                nextUrl = "http://localhost:8080/v1/cms/users?page=" + (req.getPage() + 1);
            }
        }
        res.put("next_url", nextUrl);
        res.put("users", list);

        return res;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/check/user_id")
    public ResponsDto userSetting(HttpServletResponse response, @RequestParam(name = "user_id") String userId) {

        ResponsDto res = new ResponsDto();

        UserEntity user = userService.selectUser(userId);

        if (user != null) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            res.addErrors("user_id", new String[]{"중복된 ID 입니다."});
        }

        return res;
    }

    @PostMapping("/users")
    public ResponsDto insertUser(@RequestBody UserJoinDto joinDto) {
        ResponsDto res = userService.updateUser(joinDto);
        return res;
    }

    @PutMapping("/users/{userPk}")
    public ResponsDto udpateUser(@RequestBody UserJoinDto dto, @PathVariable("userPk") long userPk) {
        dto.setId(userPk);
        ResponsDto res = userService.updateUser(dto);
        return res;
    }

    @DeleteMapping("/users/{userPk}")
    public ResponsDto deleteeUser(@PathVariable("userPk") long userPk) {
        userService.deleteUser(userPk);
        return new ResponsDto();
    }

    @GetMapping("/users/{userPk}")
    public ResponsDto userDetail(@PathVariable("userPk") long userPk) {

        UserJoinDto dto = userService.selectUserDetail(userPk);

        ResponsDto res = new ResponsDto();
        res.put("user", dto);
        return res;
    }
}
