package com.elbigs.dto;

import lombok.Data;

@Data
public class PagingParam {
    private int length;
    private int page = 1;

    private String searchStr;
    private String sortType;
    private int sortDerection; // 1 asc, 2 desc

    public int getBeginNo() {
        return (this.page - 1) * this.length;
    }
}
