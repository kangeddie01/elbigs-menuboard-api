package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shop")
public class ShopEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopId;
    private String name;
    private int status;
    private String businessNumber;
    private String phone;
    private String addr;
    private String addrDetail;
    private String shopCategory;
    private String scanImg;

    @OneToMany(mappedBy = "shopId")
    private List<ShopDeviceEntity> shopDevices;

    @Transient
    private CmsUserEntity user;

    @Transient
    private int deviceCount;

    @Transient
    private int activeDeviceCount;

    @Transient
    private int xRatioCount;

    @Transient
    private int yRatioCount;

}
