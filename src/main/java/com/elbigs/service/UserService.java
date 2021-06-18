package com.elbigs.service;

import com.elbigs.dto.*;
import com.elbigs.mybatisMapper.CommonMapper;
import com.elbigs.mybatisMapper.UserMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserMapper usersMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Value("${file.connectable-type-setting}")
    String connectableTypeSetting;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;


    public List<UserEntity> selectUserListAll(UserParamDto userParamDto) {
        List<UserEntity> list = usersMapper.selectUserListAll(userParamDto);
        return list;
    }

    public List<UserDto> selectUserList(ShopReqDto param) {
        List<UserDto> list = usersMapper.selectUserList(param);
        return list;
    }

    public UserEntity selectUser(long userPk) {
        return usersMapper.selectUser(userPk);
    }

    public UserEntity login(String userId, String password) {
//        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
//        System.out.println("hashedPw : " + hashedPw);
        UserEntity user = usersMapper.selectUserByUserId(userId);

        if (user == null) {
            return null;
        }
        boolean isSuccess = BCrypt.checkpw(password, user.getPassword());
        if (isSuccess) {
            user.setRoles(usersMapper.selectUserRoles(user.getId()));
            return user;
        } else {
            return null;
        }
    }

    public UserDetails loadUserByUsername(String userId) {

        UserEntity user = usersMapper.selectUserByUserId(userId);
        user.setRoles(usersMapper.selectUserRoles(user.getId()));

        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }
        return user;
    }

    public UserEntity selectUser(String userId) {
        UserEntity user = usersMapper.selectUserByUserId(userId);
        if (user == null) {
            return null;
        }
        user.setRoles(usersMapper.selectUserRoles(user.getId()));
        return user;
    }

    public UserJoinDto selectUserDetail(long userPk) {
        UserJoinDto user = usersMapper.selectUser(userPk);
        if (user == null) {
            return null;
        }
        return user;
    }

    private void saveFile(MultipartFile file, long fileNumber, long connectableId) {

        String dir = DateUtil.getCurrDateStr("yyyyMMdd");
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 3);
        String uploadPath = dir + "/" + convertName + "." + ext;

        System.out.println("====================================================================");
        String uploadedPath = azureBlobAdapter.upload(file, uploadPath);

        if (uploadedPath != null) {
            String fullPath = azureStorageUrl + "/" + uploadedPath;

            FileEntity fileEntity = FileEntity.builder()
                    .fileNumber(fileNumber)
                    .connectableId(connectableId)
                    .connectableType(connectableTypeSetting)
                    .originName(file.getOriginalFilename())
                    .convertName(convertName + "." + ext)
                    .fullPath(fullPath)
                    .type("file")
                    .mimeType(file.getContentType())
                    .size(String.valueOf(file.getSize()))
                    .build();

            commonMapper.deleteFiles(connectableTypeSetting, connectableId, fileNumber);
            commonMapper.insertFiles(fileEntity);
        }

    }

    @Transactional
    public ResponsDto updateUser(UserJoinDto userDto) {

        boolean isNew = false;

        // 1. validation
        // validateUserJoin();

        // 2. param set
        if (userDto.getId() <= 0) {
            isNew = true;
            userDto.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));
        }
        ResponsDto res = new ResponsDto();

        userDto.setTel(userDto.getTel());
        userDto.setPhone(userDto.getTel());
        userDto.setCode("");// not null

        if (userDto.getId() > 0) {
            usersMapper.updateUser(userDto);
        } else {
            usersMapper.insertUser(userDto);
        }

        return res;
    }

    public void deleteUser(long id) {
        usersMapper.updateUserForDelete(id);
    }

    public void updatePassword(long userPk, String password) {
        usersMapper.updatePassword(userPk, password);
    }

}

