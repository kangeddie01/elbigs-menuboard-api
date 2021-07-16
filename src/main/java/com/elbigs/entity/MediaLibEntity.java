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
    private String mediaType; // B.뱃지, V.동영상, I.이미지
    private Long mediaCategoryId;
    private Long shopId;

}
