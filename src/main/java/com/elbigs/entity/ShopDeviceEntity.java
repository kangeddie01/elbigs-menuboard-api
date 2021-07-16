package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shop_device")
public class ShopDeviceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopDeviceId;
    private Long shopId;
    private int sortNo = 0;
    private String panelId;
    private String panelMaker;
    private String panelSize;
    private String screenRatio;
    private String settopId;
    private int status; //0.deactive, 1.active, 2.modified
    private Long shopDisplayId;
    private Timestamp lastCheckAt;
}
