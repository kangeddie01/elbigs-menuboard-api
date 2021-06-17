package com.elbigs.dto;

import com.elbigs.entity.MenuCategoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MenuCategoryDto extends MenuCategoryEntity {

    /* 정렬순서 */
    private int order;

    /* 전시여부 */
    @JsonIgnore
    private boolean show;

    private String image;

    private int count;

    public void setIsShow(boolean isShow) {
        this.show = isShow;
    }

    public boolean getIsShow() {
        return this.show;
    }

}
