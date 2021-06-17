package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.MessageEntity;
import com.elbigs.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto extends MessageEntity {

    private int totalCount;
    private List<FileEntity> files = new ArrayList<>();

    public String getCreatedAt2() {
        return DateUtil.convertLocalDateToStr(this.createdAt, "yyyy-MM-dd HH:mm");
    }

    public String getUpdatedAt2() {
        return DateUtil.convertLocalDateToStr(this.updatedAt, "yyyy-MM-dd HH:mm");
    }
}
