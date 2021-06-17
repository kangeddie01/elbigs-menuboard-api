package com.elbigs.dto;

import com.elbigs.entity.BusStopEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BusStopDto extends BusStopEntity {
    private int totalCount;

    @JsonProperty("busStopId")
    private String busStopId;

    private String stopName;
    private String stopNumber;

    private List<BusRouteDto> routes;

}
