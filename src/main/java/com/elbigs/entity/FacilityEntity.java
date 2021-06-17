package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityEntity extends BaseEntity {
    private long id;
    private long userId;
    private String status;
    private String name;
    private String enName;
    private String cnName;
    private String jpName;
    protected String tel;
    private String ownerName;
    private String ownerPhone;
    private String businessNumber;
    private boolean allTime;
    private String openTime;
    private String closeTime;
    private String postCode;
    private String addr;
    private String addrDetail;
    private String addrExtra;
    private String holiday;
    private String enHoliday;
    private String cnHoliday;
    private String jpHoliday;
    private String tags;
    private String enTags;
    private String cnTags;
    private String jpTags;
    private String availableService;
    private String enAvailableService;
    private String cnAvailableService;
    private String jpAvailableService;
    private String deletedAt;

}
