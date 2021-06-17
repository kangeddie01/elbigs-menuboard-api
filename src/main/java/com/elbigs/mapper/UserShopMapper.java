package com.elbigs.mapper;

import com.elbigs.dto.ShopDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.ShopEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserShopMapper {
    List<ShopDto> selectUserShopList(ShopReqDto req);

    ShopDto selectShop(long id);

    HashMap selectShopDetailFo(Map<String, Object> param);

    void updateShop(ShopEntity shopEntity);

    void insertShop(ShopEntity shopEntity);

    void deleteShop(long id);

    List<ShopEntity> selectShopSearch(Map<String, Object> param);
}
