package com.elbigs.dto;

import lombok.Data;

@Data
public class StatisticsDto {
    private String count;
    private String eventDate;
    private int dateIndex;
    private String avg;
    private String total;
    private String macAddress;
    private String agentName;
    private String userName;
    private String maxAgent;
    private String minAgent;

}
