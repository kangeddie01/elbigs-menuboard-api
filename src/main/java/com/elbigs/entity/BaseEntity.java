package com.elbigs.entity;

import com.elbigs.util.DateUtil;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(updatable = false)
    protected Timestamp createdAt;

    @UpdateTimestamp
    protected Timestamp updatedAt;

    @Transient
    protected int totalCount;


//    public String getCreatedAt() {
//        return DateUtil.convertLocalDateToStr(this.createdAt, "yyyy-MM-dd");
//    }
//    public String getUpdatedAt() {
//        return DateUtil.convertLocalDateToStr(this.updatedAt, "yyyy-MM-dd");
//    }
}
