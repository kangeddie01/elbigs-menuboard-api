package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeEntity extends BaseEntity {
    private long id;
    private String title;
    private String content;
    private boolean noticeStatus;
    private String deletedAt;
}
