package com.elbigs.mybatisMapper;

import com.elbigs.dto.MediaLibDto;
import com.elbigs.entity.HtmlTemplateEntity;
import com.elbigs.entity.MediaLibEntity;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.TemplateCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DisplayMapper {

    List<TemplateCategoryEntity> selectTemplateCategoryList();

    List<HtmlTemplateEntity> selectHtmlTemplateList(HtmlTemplateEntity param);


}
