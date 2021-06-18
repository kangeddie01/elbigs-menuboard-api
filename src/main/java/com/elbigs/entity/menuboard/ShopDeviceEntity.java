package com.elbigs.entity.menuboard;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private int status; //0.deactive, 1.active, 2.modified
    private Long shopDisplayId;
    private String deviceSerial;
    private Timestamp lastCheckAt;
}
