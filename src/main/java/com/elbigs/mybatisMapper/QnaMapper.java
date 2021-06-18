package com.elbigs.mybatisMapper;

import com.elbigs.dto.QnaDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.QnaEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QnaMapper {
    List<QnaDto> selectQnaList(ShopReqDto req);

    QnaDto selectQna(long id);

    void updateQna(QnaEntity entity);

    void insertQna(QnaEntity entity);

    void deleteQna(long id);
}
