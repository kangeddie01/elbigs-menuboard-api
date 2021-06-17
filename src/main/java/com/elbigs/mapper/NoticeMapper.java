package com.elbigs.mapper;

import com.elbigs.dto.NoticeDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    List<NoticeDto> selectNoticeList(ShopReqDto req);

    NoticeDto selectNotice(long id);

    void updateNotice(NoticeEntity entity);

    void insertNotice(NoticeEntity entity);

    void deleteNotice(long id);
}
