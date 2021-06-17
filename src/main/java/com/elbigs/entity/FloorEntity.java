package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorEntity extends BaseEntity {

    private Long id;
    private String floorId;
    private String name;
    private int order;
    private long userId;

}
