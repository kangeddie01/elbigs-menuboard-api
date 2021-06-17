package com.elbigs.service;

import com.elbigs.dto.StatisticsDto;
import com.elbigs.mapper.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;

    public List<StatisticsDto> selectChartType1() {
        return statisticsMapper.selectChartType1();
    }

    public List<StatisticsDto> selectChartType2(long userPk) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", userPk);
        return statisticsMapper.selectChartType2(param);
    }

    public Map<String, Long> selectNetworkCount(long userPk) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", userPk);
        return statisticsMapper.selectNetworkCount(param);
    }

    public List<Map<String, Object>> selectVisitCount(long userPk) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", userPk);
        return statisticsMapper.selectVisitCount(param);
    }

    public List<StatisticsDto> selectDailyStatList(String endDate, long userPk, long dayDiff) {
        Map<String, Object> param = new HashMap<>();
        param.put("userPk", userPk);
        param.put("endDate", endDate);
        param.put("dayDiff", (int) dayDiff);
        return statisticsMapper.selectDailyStatList(param);
    }

    public List<StatisticsDto> selectDailyStatAllList(String endDate, String searchStr, long dayDiff) {
        Map<String, Object> param = new HashMap<>();
        param.put("searchStr", searchStr);
        param.put("endDate", endDate);
        param.put("dayDiff", (int) dayDiff);
        return statisticsMapper.selectDailyStatAllList(param);
    }

}
