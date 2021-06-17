package com.elbigs.service;

import com.elbigs.dto.MenuCategoryDto;
import com.elbigs.dto.MenuCategoryReqDto;
import com.elbigs.entity.MenuCategoryUserEntity;
import com.elbigs.mapper.MenuCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuCategoryService {

    @Autowired
    private MenuCategoryMapper mnuCategoryMapper;

    public List<MenuCategoryDto> selectUserMenuCategoryrList(long userPk) {
        return mnuCategoryMapper.selectUserMenuCategoryrList(userPk);
    }

    public List<MenuCategoryDto> selectUserMenuCategoryrList2(long userPk) {
        return mnuCategoryMapper.selectUserMenuCategoryrList2(userPk);
    }

    public void updateMenuCategoryShow(MenuCategoryReqDto req) {
        mnuCategoryMapper.updateMenuCategoryShow(req);
    }

    public void updateMenuCategoryOrder(MenuCategoryUserEntity entity) {
        mnuCategoryMapper.updateMenuCategoryOrder(entity);
    }
}

