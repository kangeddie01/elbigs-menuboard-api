package com.elbigs.service;

import com.elbigs.dto.UserAuthDto;
import com.elbigs.entity.CmsUserEntity;
import com.elbigs.jpaRepository.ShopRepo;
import com.elbigs.jpaRepository.CmsUserRepo;
import com.elbigs.jpaRepository.UserRolesRepo;
import com.elbigs.mybatisMapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    private ShopRepo shopRepo;

    @Autowired
    private CmsUserRepo cmsUserRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        CmsUserEntity cmsUserEntity = cmsUserRepo.findByLoginId(loginId);

        if (cmsUserEntity != null && cmsUserEntity.getStatus() == 1) {
            UserAuthDto user = new UserAuthDto();
            user.setPassword(cmsUserEntity.getPassword());
            user.setLoginId(cmsUserEntity.getLoginId());
            user.setRoles(userMapper.selectUserRoles(user.getLoginId()));
            return user;
        } else {
            throw new UsernameNotFoundException(loginId);
        }

    }


    public CmsUserEntity login(String loginId, String password) {

        CmsUserEntity cmsUserEntity = cmsUserRepo.findByLoginId(loginId);

        if (cmsUserEntity == null) {
            return null;
        }
        boolean isSuccess = BCrypt.checkpw(password, cmsUserEntity.getPassword());
        if (isSuccess) {
            cmsUserEntity.setUserRoles(userMapper.selectUserRoles(loginId));
            return cmsUserEntity;
        } else {
            return null;
        }
    }
}

