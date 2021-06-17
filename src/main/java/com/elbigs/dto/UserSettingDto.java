package com.elbigs.dto;

import com.elbigs.entity.UserSettingEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettingDto extends UserSettingEntity {
    private String logoImg;
    private String storeImg;
    private String trafficImg;
    private String facilitiesImg;
    private String attractionsImg;
    private String tel_1;
    private String tel_2;
    private String tel_3;

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
}
