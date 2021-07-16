package com.elbigs.mybatisMapper;

import com.elbigs.dto.PagingParam;
import com.elbigs.entity.ShopEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopEntity> selectShopList(PagingParam param);

}
