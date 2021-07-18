package com.elbigs.dto;

import com.elbigs.entity.ShopDeviceEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class ShopDeviceDto extends ShopDeviceEntity {

    private String previewImagePath;
    private String displayName;
}
