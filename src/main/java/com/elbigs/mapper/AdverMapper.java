package com.elbigs.mapper;

import com.elbigs.dto.AdverDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.AdverEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdverMapper {
    List<AdverDto> selectAdverList(ShopReqDto req);

    AdverDto selectAdver(long id);

    void updateAdver(AdverEntity entity);

    void insertAdver(AdverEntity entity);

    void deleteAdver(long id);

    List<HashMap> selectAdListByKiosk(Map<String, Object> param);
}
