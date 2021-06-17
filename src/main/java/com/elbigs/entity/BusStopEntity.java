package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusStopEntity extends BaseEntity {
    private long id;
    private long userId;
    private String name;
    private String number;
    private String busStopId;
    private String cityCode;
    private String cityName;
    private String lat;
    private String lng;
    private String deletedAt;

}
