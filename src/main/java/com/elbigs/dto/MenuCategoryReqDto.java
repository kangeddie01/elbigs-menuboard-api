package com.elbigs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MenuCategoryReqDto {
    //    private int is_show;
    private String _method;
    private long userPk;
    private long categoryId;
    private boolean show;

    public void setIs_show(int isShow) {
        if (isShow == 1) {
            this.show = true;
        } else {
            this.show = false;
        }
    }

    public boolean getShow() {
        return this.show;
    }
}
