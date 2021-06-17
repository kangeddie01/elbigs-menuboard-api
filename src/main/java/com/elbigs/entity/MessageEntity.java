package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity extends BaseEntity {
    private long id;
    private String title;
    private String content;
    private String deletedAt;
}
