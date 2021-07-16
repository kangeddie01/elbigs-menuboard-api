package com.elbigs.mybatisMapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<String> selectUserRoles(String loginId);
}
