package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.LineNoticeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineNoticeDto extends LineNoticeEntity {

    // 리스트
    private int totalCount;
    private List<FileEntity> files;

}
