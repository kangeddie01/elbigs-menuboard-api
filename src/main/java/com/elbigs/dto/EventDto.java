package com.elbigs.dto;

import com.elbigs.entity.EventEntity;
import com.elbigs.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto extends EventEntity {

    // 리스트
    private int totalCount;
    private List<FileEntity> files;

    private String tel_1;
    private String tel_2;
    private String tel_3;

    private String shopName;

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
