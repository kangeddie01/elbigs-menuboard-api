package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryShopEntity extends BaseEntity {

    private long id;
    private long menuCategoryId;
    private long shopId;

}
