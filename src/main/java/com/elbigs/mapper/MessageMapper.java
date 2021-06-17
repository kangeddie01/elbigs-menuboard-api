package com.elbigs.mapper;

import com.elbigs.dto.MessageDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<MessageDto> selectMessageList(ShopReqDto req);

    MessageDto selectMessage(long id);

    void updateMessage(MessageEntity entity);

    void insertMessage(MessageEntity entity);

    void deleteMessage(long id);
}
