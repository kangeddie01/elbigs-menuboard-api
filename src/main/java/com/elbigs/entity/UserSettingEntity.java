package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingEntity extends BaseEntity {
    private long id;
    private long userId;
    private String slogan1;
    private String enSlogan1;
    private String cnSlogan1;
    private String jpSlogan1;
    private boolean slogan1Bold;

    private String slogan2;
    private String enSlogan2;
    private String cnSlogan2;
    private String jpSlogan2;
    private boolean slogan2Bold;

    private String slogan3;
    private String enSlogan3;
    private String cnSlogan3;
    private String jpSlogan3;
    private boolean slogan3Bold;

    private String tel;
    private String openTime;
    private String closeTime;
}
