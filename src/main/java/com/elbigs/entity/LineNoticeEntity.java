package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineNoticeEntity extends BaseEntity {
    private long id;
    private long userId;
    private String title;
    private String enTitle;
    private String cnTitle;
    private String jpTitle;
    private String comment;
    private String enComment;
    private String cnComment;
    private String jpComment;
    private boolean status;
    private boolean periodStatus;
    private String type;
    private String startDate;
    private String endDate;
    private String deletedAt;

}
