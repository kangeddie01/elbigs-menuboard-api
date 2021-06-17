package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusRouteEntity extends BaseEntity {
    private long id;
    private String routeId;
    private String routeNumber;
    private String nextBusStop;
    private String busType;
    private long arriveTime;
    private long busStopId;

}
