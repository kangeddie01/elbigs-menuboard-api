package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shop_display")
public class ShopDisplayEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopDisplayId;
    private Long shopId;
    private int status;
    private String screenRatio;
    private String displayName;
//    private String displayHtml;
    private String downloadPath;
    private String previewImagePath;
}
