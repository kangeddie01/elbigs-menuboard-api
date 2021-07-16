package com.elbigs.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "html_template")
public class HtmlTemplateEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long htmlTemplateId;
    private String name;
    private String title;
    private String screenRatio;
    private String templateType; // a.일반, b.sns, c.vimeo
    private Long templateCategoryId;
    private String html;
    private String templateZipPath;
    private String previewImagePath;
    private String downloadPath;
    private String recommendYn;

}
