package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaEntity extends BaseEntity {
    private long id;
    private long userId;
    private String title;
    private String content;
    private String comment;
    private String deletedAt;
}
