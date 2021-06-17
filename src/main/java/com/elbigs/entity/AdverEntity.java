package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdverEntity extends BaseEntity {
    private long id;
    private long userId;
    private String title;
    private boolean isAll;
    private boolean isReserved;
    private String startDate;
    private String endDate;
}
