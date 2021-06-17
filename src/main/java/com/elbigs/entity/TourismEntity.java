package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourismEntity extends BaseEntity {
    private long id;
    private long userId;
    private String name;
    private String enName;
    private String cnName;
    private String jpName;
    private String information;
    private String enInformation;
    private String cnInformation;
    private String jpInformation;
    private String addr;
    private String enAddr;
    private String cnAddr;
    private String jpAddr;
    private String holiday;
    private String enHoliday;
    private String cnHoliday;
    private String jpHoliday;
    private String charges;
    private String enCharges;
    private String cnCharges;
    private String jpCharges;
    private String time;
    private String enTime;
    private String cnTime;
    private String jpTime;
    private String deletedAt;

}
