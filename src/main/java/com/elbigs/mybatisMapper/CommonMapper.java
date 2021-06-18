package com.elbigs.mybatisMapper;

import com.elbigs.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonMapper {


    List<FileEntity> selectFileList(String connectableType, long connectableId);

    void deleteFiles(String connectableType, long connectableId, long fileNumber);

    void insertFiles(FileEntity fileEntity);


}
