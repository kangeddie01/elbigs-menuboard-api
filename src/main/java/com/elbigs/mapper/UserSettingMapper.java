package com.elbigs.mapper;

import com.elbigs.dto.UserSettingDto;
import com.elbigs.entity.UserSettingEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingMapper {
    UserSettingDto selectUserSetting(long userId);

    void updateUserSetting(UserSettingEntity entity);

    void insertUserSetting(UserSettingEntity entity);

    Long selectUserSettingId(long userId);
}
