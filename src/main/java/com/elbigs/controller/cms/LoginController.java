package com.elbigs.controller.cms;

import com.elbigs.config.security.JwtTokenProvider;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ResponseDto2;
import com.elbigs.entity.LoginParam;
import com.elbigs.entity.UserEntity;
import com.elbigs.exception.LoginFailedException;
import com.elbigs.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3300") // 추가
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cms")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/auth/login")
    public ResponseDto2 login(@RequestBody LoginParam login) {


        UserEntity user = userService.login(login.getUserId(), login.getPassword());
        ResponseDto2<UserEntity> res = new ResponseDto2();

        if (user == null) {
//            Map<String, Object> errors = new HashMap<>();
//            String[] arr = {"계정 정보 확인 부탁드립니다."};
            throw new LoginFailedException();
//            errors.put("user_id", arr);
//            res.setSuccess(false);
//            res.setErrors(errors);
        } else {
            res.setSuccess(true);
            res.setData(user);

            List<String> r = new ArrayList<>();
            r.add("USER");
            r.add("ADMIN");
            r.add("ROLE_ADMIN");
            r.add("ROLE_USER");
            String token = jwtTokenProvider.createToken(String.valueOf(user.getUserId()), r);

            Authentication auth = jwtTokenProvider.getAuthentication(token);

            res.setToken(token);
        }
        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/auth/logout")
    public ResponsDto logout(HttpServletRequest request) {
        return new ResponsDto();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/auth/me")
    public ResponsDto loginInfo(HttpServletRequest request) {
//        ResponseDto<User> res = new ResponseDto();

        String token = jwtTokenProvider.resolveToken(request);
        Map<String, Object> resMap = new HashMap<String, Object>();

        ResponsDto res2 = new ResponsDto();
        if (jwtTokenProvider.validateToken(token)) {
            UserEntity user = userService.selectUser(jwtTokenProvider.getUserPk(token));
            resMap.put("email", user.getEmail());
            resMap.put("id", user.getId());
            resMap.put("isAd", user.isAd());
            resMap.put("isMaster", user.isMasterYn());
            resMap.put("name", user.getName());
            resMap.put("tel", user.getTel());
//            res.setUser(resMap);
//            res.setSuccess(true);

            res2.put("user", resMap);
        }
        return res2;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/users/{userPk}/auth/password")
    public ResponsDto loginInfo(HttpServletResponse response, @PathVariable("userPk") long userPk
            , @RequestParam("old_password") String oldPassword
            , @RequestParam("new_password") String newPassword
            , @RequestParam("new_password_confirm") String newPasswordConfirm) {


        ResponsDto res = validateChangePassword(userPk, oldPassword, newPassword, newPasswordConfirm);
        if (!res.isSuccess()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        } else {
            userService.updatePassword(userPk, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }
        return res;
    }

    private ResponsDto validateChangePassword(long userPk, String oldPassword, String newPassword, String newPasswordConfirm) {

        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());// 필수 항목
        String requireMsg1 = messageSource.getMessage("error.msg.change-password1", null, LocaleContextHolder.getLocale());// 신규패스워드 불일치
        String requireMsg2 = messageSource.getMessage("error.msg.change-password2", null, LocaleContextHolder.getLocale());// 이전 패스워드 불일치
        String requireMsg3 = messageSource.getMessage("error.msg.change-password3", null, LocaleContextHolder.getLocale());// 신규 패스워드 6자 이상

        ResponsDto res = new ResponsDto();

        // 필수 체크
        if (!StringUtils.hasLength(oldPassword)) {
            res.addErrors("old_password", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(newPassword)) {
            res.addErrors("new_password", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(newPasswordConfirm)) {
            res.addErrors("new_password_confirm", new String[]{requireMsg});
        }
        if (!res.isSuccess()) {
            return res;
        }
        // 이전 비밀번호 체크
        UserEntity user = userService.selectUser(userPk);
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            res.addErrors("old_password", new String[]{requireMsg2});
            return res;
        }
        // 6자리 이상
        if (newPassword.length() < 6) {
            res.addErrors("new_password", new String[]{requireMsg3});
        }
        if (newPasswordConfirm.length() < 6) {
            res.addErrors("new_password_confirm", new String[]{requireMsg3});
        }
        if (!res.isSuccess()) {
            return res;
        }
        // 신규 패스워드 불일치
        if (!newPasswordConfirm.equals(newPassword)) {
            res.addErrors("new_password", new String[]{requireMsg1});
            res.addErrors("new_password_confirm", new String[]{requireMsg1});
        }

        return res;
    }
}
