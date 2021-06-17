package com.elbigs.dto;

import com.elbigs.entity.EventEntity;
import com.elbigs.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdverDto extends EventEntity {

    // 리스트
    private int totalCount;
    private List<FileEntity> files;

}
