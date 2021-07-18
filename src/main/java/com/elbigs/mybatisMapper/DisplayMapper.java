package com.elbigs.mybatisMapper;

import com.elbigs.entity.MediaLibEntity;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.TemplateCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DisplayMapper {
    List<MediaLibEntity> selectMediaLibList1(Map<String, Long> param);

    List<TemplateCategoryEntity> selectTemplateCategoryList();
}
