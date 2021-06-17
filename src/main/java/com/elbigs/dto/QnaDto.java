package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.QnaEntity;
import com.elbigs.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QnaDto extends QnaEntity {

    private int totalCount;
    private List<FileEntity> files = new ArrayList<>();

    public String getCreatedAt2() {
        return DateUtil.convertLocalDateToStr(this.createdAt, "yyyy-MM-dd HH:mm");
    }

    public String getUpdatedAt2() {
        return DateUtil.convertLocalDateToStr(this.updatedAt, "yyyy-MM-dd HH:mm");
    }

    public String getCommentStatus() {
        if (getComment() == null) {
            return "미답변";
        } else {
            return null;
        }
    }
}
