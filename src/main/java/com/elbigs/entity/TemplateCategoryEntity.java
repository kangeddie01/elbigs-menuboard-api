package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "template_category")
public class TemplateCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateCategoryId;
    private String name;
    private Long upperCategoryId;
    private int sortNo;

}
