package com.elbigs.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ListResponseDto<T>{

    private List<T> list;
    private int page;
    private int length;
    private int totalCount;
    private int nextPageNo;

    public int getNextPageNo() {
        if (this.totalCount > this.page * this.length) {
            return this.page + 1;
        } else {
            return 0;
        }
    }
}
