package com.elbigs.mapper;

import com.elbigs.dto.ShopReqDto;
import com.elbigs.dto.UserDto;
import com.elbigs.dto.UserJoinDto;
import com.elbigs.dto.UserParamDto;
import com.elbigs.entity.FloorEntity;
import com.elbigs.entity.KioskEntity;
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

    void insertFloor(FloorEntity entity);

    void insertKiosk(KioskEntity entity);

    void updateKiosk(KioskEntity entity);

    void deleteFloor(long userPk);

    void deleteKioskById(long id);

    void updateUserForDelete(long id);

    List<KioskEntity> selectKioskList(long userPk);

    List<FloorEntity> selectFloorList(long userPk);

    void updatePassword(long id, String password);

    KioskEntity selectKioskByMac(String macAddress);

    KioskEntity selectKioskByKey(String key);
}
