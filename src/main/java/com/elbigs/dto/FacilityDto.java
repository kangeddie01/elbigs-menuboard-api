package com.elbigs.dto;

import com.elbigs.entity.FacilityEntity;
import com.elbigs.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDto extends FacilityEntity {

    // 리스트
    private String category;
    private int totalCount;

    private List<FileEntity> files;
    private List<Long> menuCategoryIds;

    private String tel_1;
    private String tel_2;
    private String tel_3;

    public String getTel_1() {
        if (this.tel != null) {
            return this.tel.split("-")[0];
        } else {
            return null;
        }
    }

    public String getTel_2() {
        if (this.tel != null) {
            return this.tel.split("-")[1];
        } else {
            return null;
        }
    }

    public String getTel_3() {
        if (this.tel != null) {
            return this.tel.split("-")[2];
        } else {
            return null;
        }
    }

//    public void setTel(String tel) {
//
//        try {
//            if (tel != null) {
//                this.tel_1 = tel.split("-")[0];
//                this.tel_2 = tel.split("-")[1];
//                this.tel_3 = tel.split("-")[2];
//            }
//        } catch (Exception e) {
//
//        }
//    }

}
