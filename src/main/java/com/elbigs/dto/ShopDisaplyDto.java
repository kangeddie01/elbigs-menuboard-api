package com.elbigs.dto;

import com.elbigs.entity.menuboard.ShopDisplayEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class ShopDisaplyDto extends ShopDisplayEntity {

    private String previewDataUrl;

}
