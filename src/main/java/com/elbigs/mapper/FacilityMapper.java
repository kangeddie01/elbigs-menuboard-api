package com.elbigs.mapper;

import com.elbigs.dto.FacilityDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.FacilityEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface FacilityMapper {
    List<FacilityDto> selectFacilityList(ShopReqDto req);

    FacilityDto selectFacility(long id);

    void updateFacility(FacilityEntity entity);

    void insertFacility(FacilityEntity entity);

    void deleteFacility(long id);

    HashMap selectFacilityDetailFo(Map<String, Object> param);
}
