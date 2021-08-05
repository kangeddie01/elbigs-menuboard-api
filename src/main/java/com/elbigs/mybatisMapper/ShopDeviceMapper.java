package com.elbigs.mybatisMapper;

import com.elbigs.dto.ShopDeviceDto;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.ShopDisplayEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShopDeviceMapper {
    void updateShopDeviceDisplay(ShopDeviceEntity entity);
    List<ShopDeviceDto> selectShopDeviceList(Long shopId);
    void updateLastCheckAt(Long shopDeviceId);
    void updateDeviceStatusToModify(Long shopDisplayId);
}
