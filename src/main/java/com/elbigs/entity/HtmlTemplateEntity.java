package com.elbigs.entity;

import lombok.*;

import javax.persistence.*;

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
    private String previewImagePath;
    private String templateZipPath;
    private String downloadPath;
    private String recommendYn;

    @Transient
    private String categoryName;
}
