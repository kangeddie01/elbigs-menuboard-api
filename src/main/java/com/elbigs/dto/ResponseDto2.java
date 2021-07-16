package com.elbigs.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResponseDto2<T> extends CommonResponseDto{

    private T data;
    private String token;
    private Map user;
    private List<Map<String, Object>> users;

}
