package com.elbigs.dto;

import com.elbigs.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserJoinDto extends UserEntity {

    @JsonProperty("user_id_confirm")
    private boolean userIdConfirm;

    private String passwordConfirm;

    private String tel_1;
    private String tel_2;
    private String tel_3;

    @JsonProperty("isAd")
    private boolean isAd;

    @JsonProperty("naviSpeed")
    private int naviSpeed;


}
