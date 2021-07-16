package com.elbigs.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoLombok<T> {
    private List<T> list;

    private int page;
    private int length;
    private int totalCount;
    private int nextPageNo;

}
