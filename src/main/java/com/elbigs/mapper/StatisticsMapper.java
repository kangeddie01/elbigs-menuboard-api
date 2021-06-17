package com.elbigs.mapper;

import com.elbigs.dto.StatisticsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {
    List<StatisticsDto> selectChartType1();

    List<StatisticsDto> selectChartType2(Map<String, Object> param);

    Map<String, Long> selectNetworkCount(Map<String, Object> param);

    List<Map<String, Object>> selectVisitCount(Map<String, Object> param);

    List<StatisticsDto> selectDailyStatList(Map<String, Object> param);

    List<StatisticsDto> selectDailyStatAllList(Map<String, Object> param);


}
