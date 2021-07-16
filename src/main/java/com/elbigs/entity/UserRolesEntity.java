package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_roles")
@IdClass(UserRolesId.class)
public class UserRolesEntity {
    @Id
    String loginId;
    @Id
    String roles;
}
