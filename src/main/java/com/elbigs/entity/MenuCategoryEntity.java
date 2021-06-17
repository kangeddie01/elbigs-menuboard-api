package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryEntity extends BaseEntity {

    private long id;
    private String name;
    private String enName;
    private String cnName;
    private String jpName;
    private int type;

}
