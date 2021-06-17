package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KioskEntity extends BaseEntity {

    private long id;
    private long userId;
    private String key;
    private String macAddress;
    private String addr;
    private int order;
    private long x;
    private long y;
    private boolean active;
    private String poiId;
    private String floorId;
    private String floor;
    private String agentName;


}
