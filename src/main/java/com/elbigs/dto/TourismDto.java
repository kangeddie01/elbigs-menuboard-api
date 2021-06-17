package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.TourismEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourismDto extends TourismEntity {

    // 리스트
    private int totalCount;
    private List<FileEntity> files;

    private List<Map<String, String>> qrImg;

    private List<String> banners;

}
