package com.elbigs.mapper;

import com.elbigs.dto.ShopReqDto;
import com.elbigs.dto.TourismDto;
import com.elbigs.entity.TourismEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TourismMapper {
    List<TourismDto> selectTourismList(ShopReqDto req);

    TourismDto selectTourism(long id);

    void updateTourism(TourismEntity entity);

    void insertTourism(TourismEntity entity);

    void deleteTourism(long id);

    List<TourismDto> selectTourismListByLang(ShopReqDto req);
}
