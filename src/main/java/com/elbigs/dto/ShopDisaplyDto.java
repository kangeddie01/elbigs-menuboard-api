package com.elbigs.dto;

import com.elbigs.entity.ShopDisplayEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class ShopDisaplyDto extends ShopDisplayEntity {

    private String previewDataUrl;
    private Long htmlTemplateId;
    private String displayHtml;

}
