package com.elbigs.controller.front;

import com.elbigs.dto.*;
import com.elbigs.entity.*;
import com.elbigs.mybatisMapper.*;
import com.elbigs.service.*;
import com.elbigs.util.ElbigsUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kiosk")
public class BigsApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @GetMapping("/users")
    public List<UserEntity> selectUserList(UserParamDto userParamDto) {
        return userService.selectUserListAll(userParamDto);
    }

}
