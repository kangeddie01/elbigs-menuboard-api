package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.ShopEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopDto extends ShopEntity {

    // 리스트
    private String category;
    private int shopTotalCount;


    private List<FileEntity> files;
    private List<Long> initialIds;
    private List<Long> menuCategoryIds;

    private String tel_1;
    private String tel_2;
    private String tel_3;

    public void setIsSmartOrder(int isSmartOrder) {
        if (isSmartOrder == 1) {
            super.setSmartOrderYn(true);
        } else {
            super.setSmartOrderYn(false);
        }
    }

    public boolean getIsSmartOrder() {
        return super.isSmartOrderYn();
    }

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
