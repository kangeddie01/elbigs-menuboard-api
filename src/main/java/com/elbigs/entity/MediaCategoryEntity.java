package com.elbigs.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "media_category")
public class MediaCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaCategoryId;
    private String mediaCategoryName;
    private String categoryType;// cm.일반, bg.배경화면, bd.뱃지
    private Long shopId;
    private int sortNo;



}
