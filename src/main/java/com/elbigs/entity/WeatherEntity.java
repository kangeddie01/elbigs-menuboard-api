package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherEntity extends BaseEntity {
    private long id;
    private long userId;
    private String date;
    private String time;
    private String sky;
    private String imgFile;
    private String temperature;
}
