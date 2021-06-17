package com.elbigs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class CommonResponseDto {

    boolean success;
    private Map<String, Object> errors;

}
