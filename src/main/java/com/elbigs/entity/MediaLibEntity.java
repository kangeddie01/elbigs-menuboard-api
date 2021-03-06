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
@Entity(name = "media_lib")
public class MediaLibEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaLibId;
    private String mediaPath;
    private String thumbnailPath;
    private String mediaType; // v.동영상, i.이미지
    private Long mediaCategoryId;
    private Long shopId;
    private Long size;
    private String orginFilename;
    private String resolution;


}
