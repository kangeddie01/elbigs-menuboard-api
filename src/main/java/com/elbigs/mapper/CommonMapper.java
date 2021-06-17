package com.elbigs.mapper;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.InitialSearchEntity;
import com.elbigs.entity.InitialSearchShopEntity;
import com.elbigs.entity.WeatherEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonMapper {
    List<InitialSearchEntity> selectInitialSearchList();

    List<Long> selectInitialSearchShopList(long shopId);

    List<FileEntity> selectFileList(String connectableType, long connectableId);

    void deleteFiles(String connectableType, long connectableId, long fileNumber);

    void insertFiles(FileEntity fileEntity);

    void deleteInitialSearchShop(long shopId);

    void insertInitialSearchShop(InitialSearchShopEntity entity);

    List<WeatherEntity> selectWeatherList(long userPk);

    void insertWeather(WeatherEntity entity);
}
