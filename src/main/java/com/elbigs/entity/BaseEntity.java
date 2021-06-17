package com.elbigs.entity;

import com.elbigs.util.DateUtil;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public abstract class BaseEntity implements Serializable {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public String getCreatedAt() {
        return DateUtil.convertLocalDateToStr(this.createdAt, "yyyy-MM-dd");
    }

    public String getUpdatedAt() {
        return DateUtil.convertLocalDateToStr(this.updatedAt, "yyyy-MM-dd");
    }
}
