package com.elbigs.dto;

import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.ShopEntity;
import lombok.Data;

import java.util.List;

@Data
public class ShopDto extends ShopEntity {
    int totalCount;
    private List<ShopDeviceEntity> shopDevices;
}
