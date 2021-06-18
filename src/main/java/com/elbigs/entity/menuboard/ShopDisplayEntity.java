package com.elbigs.entity.menuboard;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

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
    private String displayName;
    private String displayHtml;
    private String downloadPath;
    private String previewImagePath;
}
