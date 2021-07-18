package com.elbigs.controller.cms;

import com.elbigs.config.security.JwtTokenProvider;
import com.elbigs.dto.ResponseDto2;
import com.elbigs.dto.UserAuthDto;
import com.elbigs.entity.CmsUserEntity;
import com.elbigs.exception.LoginFailedException;
import com.elbigs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cms/login")
public class LoginController {


    @Autowired
    private UserService userService;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseDto2 login(@RequestBody UserAuthDto login) {
        CmsUserEntity user = userService.login(login.getLoginId(), login.getPassword());
        ResponseDto2<CmsUserEntity> res = new ResponseDto2();

        if (user == null) {
            throw new LoginFailedException();
        } else {
            res.setSuccess(true);
            res.setData(user);

            String token = jwtTokenProvider.createToken(String.valueOf(login.getLoginId()), user.getUserRoles());

//            Authentication auth = jwtTokenProvider.getAuthentication(token);

            res.setToken(token);
        }
        return res;
    }
}
