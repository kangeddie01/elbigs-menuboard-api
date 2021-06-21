package com.elbigs.mybatisMapper;

import com.elbigs.entity.menuboard.ShopDeviceEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopDeviceMapper {
    void updateShopDeviceDisplay(ShopDeviceEntity entity);
}
