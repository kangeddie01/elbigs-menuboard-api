package com.elbigs.dto;

import lombok.Data;

@Data
public class PagingParam {
    private int length;
    private int beginNo;
    private int page = 1;

    public int getBeginNo() {
        return (this.page - 1) * this.length;
    }
}
