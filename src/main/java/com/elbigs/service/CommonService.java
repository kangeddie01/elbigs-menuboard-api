package com.elbigs.service;

import com.elbigs.entity.InitialSearchEntity;
import com.elbigs.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    public List<InitialSearchEntity> selectInitialSearchList() {
        return commonMapper.selectInitialSearchList();
    }
}
