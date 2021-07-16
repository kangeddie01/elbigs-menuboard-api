package com.elbigs.mybatisMapper;

import com.elbigs.dto.ShopDeviceDto;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.ShopDisplayEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopDeviceMapper {
    void updateShopDeviceDisplay(ShopDeviceEntity entity);
    List<ShopDeviceDto> selectShopDeviceList(Long shopId);
}
