package com.elbigs.dto;

import com.elbigs.entity.FloorEntity;
import com.elbigs.entity.KioskEntity;
import com.elbigs.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserJoinDto extends UserEntity {
    private List<KioskEntity> kiosks;
    private List<FloorEntity> floors;

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

    public void setTel(String tel) {

        try {
            if (tel != null) {
                this.tel_1 = tel.split("-")[0];
                this.tel_2 = tel.split("-")[1];
                this.tel_3 = tel.split("-")[2];
            }
        } catch (Exception e) {

        }
    }

    public String getTel() {

        if (tel_1 != null && tel_2 != null && tel_3 != null) {
            return tel_1 + "-" + tel_2 + "-" + tel_3;
        } else {
            return null;
        }
    }

    public String getPhone() {

        if (tel_1 != null && tel_2 != null && tel_3 != null) {
            return tel_1 + "-" + tel_2 + "-" + tel_3;
        } else {
            return null;
        }
    }

}
