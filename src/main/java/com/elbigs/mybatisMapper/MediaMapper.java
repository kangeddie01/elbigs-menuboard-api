package com.elbigs.mybatisMapper;

import com.elbigs.dto.MediaLibDto;
import com.elbigs.entity.MediaCategoryEntity;
import com.elbigs.entity.MediaLibEntity;
import com.elbigs.entity.TemplateCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MediaMapper {
    List<MediaLibDto> selectMediaLibList(MediaLibEntity param);

    List<MediaCategoryEntity> selectMediaCategoryList(MediaCategoryEntity param);
}
