package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryUserEntity extends BaseEntity {

    private long id;
    private long menuCategoryId;
    private long userId;
    private int order = -1;
    private boolean isShow;

    public MenuCategoryUserEntity(Long userId) {
        this.userId = userId;
    }
}
