package com.elbigs.mapper;

import com.elbigs.dto.EventDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.EventEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventMapper {
    List<EventDto> selectEventList(ShopReqDto req);

    EventDto selectEvent(long id);

    void updateEvent(EventEntity entity);

    void insertEvent(EventEntity entity);

    void deleteEvent(long id);
}
