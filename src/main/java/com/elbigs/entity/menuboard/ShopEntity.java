package com.elbigs.entity.menuboard;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private String loginId;
    private String password;
    private int status;
    private String shopTypeCd;
    private String shopCategoryCd;
    private String addr;
    private String shopImg1;
    private String shopImg2;

    @OneToMany(mappedBy = "shopId")
    private List<ShopDeviceEntity> shopDevices;

}
