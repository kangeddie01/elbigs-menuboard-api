package com.elbigs.batch;

import com.elbigs.dto.UserParamDto;
import com.elbigs.entity.UserEntity;
import com.elbigs.service.UserService;
import com.elbigs.service.webclient.WebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class BatchTest {
    @Autowired
    private WebClientService webClientService;

    @Autowired
    private UserService userService;

    @GetMapping("/test1")
    public String test() {
        UserParamDto userParamDto = new UserParamDto();
        List<UserEntity> list = userService.selectUserListAll(null);
        String result = "";
        for (UserEntity user : list) {
            boolean success = webClientService.syncWeather(user);
            if (success) {
                result += user.getUserId() + " : 성공<br/>";
            } else {
                result += user.getUserId() + " : 실패<br/>";
            }
        }
        return result;

    }
}
