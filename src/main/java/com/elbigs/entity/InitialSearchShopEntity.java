package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InitialSearchShopEntity extends BaseEntity {

    private long id;
    private long shopId;
    private long initialSearchId;

}
