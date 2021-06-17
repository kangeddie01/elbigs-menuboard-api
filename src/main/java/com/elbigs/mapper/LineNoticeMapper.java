package com.elbigs.mapper;

import com.elbigs.dto.EventDto;
import com.elbigs.dto.LineNoticeDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.LineNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface LineNoticeMapper {
    List<LineNoticeDto> selectLineNoticeList(ShopReqDto req);

    EventDto selectLineNotice(long id);

    void updateLineNotice(LineNoticeEntity entity);

    void insertLineNotice(LineNoticeEntity entity);

    void deleteLineNotice(long id);

    List<String> selectLineNoticeListForFo(long userPk);

    HashMap selectPopupNoticeListForFo(long userPk);
}
