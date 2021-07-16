package com.elbigs.dto;

import com.elbigs.entity.MediaLibEntity;
import lombok.Data;

@Data
public class MediaLibDto extends MediaLibEntity {
    private String mediaCategoryName;
}
