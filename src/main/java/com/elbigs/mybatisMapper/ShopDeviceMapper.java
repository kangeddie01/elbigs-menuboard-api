package com.elbigs.mybatisMapper;

import com.elbigs.dto.NoticeDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.NoticeEntity;
import com.elbigs.entity.menuboard.ShopDeviceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopDeviceMapper {
    void updateShopDeviceDisplay(ShopDeviceEntity entity);
}
