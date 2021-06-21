package com.elbigs.batch;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class BatchTest {

    @GetMapping("/test1")
    public String test() {

        return "result";

    }
}
