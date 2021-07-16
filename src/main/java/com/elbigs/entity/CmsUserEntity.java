package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cms_user")
public class CmsUserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private int status;
    private String loginId;
    private String password;
    private String phone;
    private String email;
    private Long shopId;

    @Transient
    private List<String> userRoles;

    @Transient
    private String passwordConfirm;
}
