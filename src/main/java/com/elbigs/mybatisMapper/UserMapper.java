package com.elbigs.mybatisMapper;

import com.elbigs.dto.ShopReqDto;
import com.elbigs.dto.UserDto;
import com.elbigs.dto.UserJoinDto;
import com.elbigs.dto.UserParamDto;
import com.elbigs.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<UserEntity> selectUserListAll(UserParamDto paramDto);

    List<UserDto> selectUserList(ShopReqDto dto);

    UserEntity selectUserByUserId(String userId);

    UserJoinDto selectUser(long id);

    UserEntity findByMemberId(String userId);

    List<String> selectUserRoles(long userId);

    void updateUser(UserEntity entity);

    void insertUser(UserEntity entity);

    void updateUserForDelete(long id);

    void updatePassword(long id, String password);
}
