package com.elbigs.dto;

import lombok.Data;

@Data
public class BusRouteDto {
    private String routeNumber;
    private String nextBusStop;
    private String busType;
    private String busTypeColor;
}
