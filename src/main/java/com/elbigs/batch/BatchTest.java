package com.elbigs.batch;

import com.elbigs.service.UserService;
import com.elbigs.service.webclient.WebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class BatchTest {

    @GetMapping("/test1")
    public String test() {

        return "result";

    }
}
