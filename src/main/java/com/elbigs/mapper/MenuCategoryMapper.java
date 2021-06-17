package com.elbigs.mapper;

import com.elbigs.dto.MenuCategoryDto;
import com.elbigs.dto.MenuCategoryReqDto;
import com.elbigs.entity.MenuCategoryFacilityEntity;
import com.elbigs.entity.MenuCategoryShopEntity;
import com.elbigs.entity.MenuCategoryUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuCategoryMapper {
    List<MenuCategoryDto> selectUserMenuCategoryrList(long userPk);

    List<MenuCategoryDto> selectUserMenuCategoryrList2(long userPk);

    void updateMenuCategoryShow(MenuCategoryReqDto req);

    void updateMenuCategoryOrder(MenuCategoryUserEntity entity);

    List<Long> selectUserMenuCategoryShopList(long shopId);

    void deleteMenuCategoryShop(long shopId);

    void insertMenuCategoryShop(MenuCategoryShopEntity entity);

    List<Long> selectUserMenuCategoryFacilityList(long facilityId);

    void deleteMenuCategoryFacility(long facilityId);

    void insertMenuCategoryFacility(MenuCategoryFacilityEntity entity);

    List<MenuCategoryDto> selectUserMenuCategoryForFo(long userPk);
}
