package com.elbigs.mapper;

import com.elbigs.dto.BusStopDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.BusRouteEntity;
import com.elbigs.entity.BusStopEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusStopMapper {
    List<BusStopDto> selectBusStopList(ShopReqDto req);

    List<BusRouteEntity> selectBusRouteList(long busStopPk);

    BusStopDto selectBusStop(long id);

    void updateBusStop(BusStopEntity entity);

    void insertBusStop(BusStopEntity entity);

    void deleteBusStop(long id);

    List<BusStopDto> selectBusInfos(long userPk);
}
