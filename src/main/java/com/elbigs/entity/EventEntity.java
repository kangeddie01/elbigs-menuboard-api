package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity extends BaseEntity {
    private long id;
    private long userId;
    private boolean shopStatus;
    private long shopId;
    private String tel;
    private String title;
    private String enTitle;
    private String cnTitle;
    private String jpTitle;
    private String content;
    private String enContent;
    private String cnContent;
    private String jpContent;
    private String startDate;
    private String endDate;
    private String deletedAt;

//
//    public String getStartDate() {
//        return DateUtil.convertLocalDateToStr(this.startDate, "yyyy-MM-dd");
//    }
//
//    public String getEndDate() {
//        return DateUtil.convertLocalDateToStr(this.endDate, "yyyy-MM-dd");
//    }

}
