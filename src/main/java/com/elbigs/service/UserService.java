package com.elbigs.service;

import com.elbigs.dto.*;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.UserMapper;
import com.elbigs.mapper.UserSettingMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.entity.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserMapper usersMapper;
    @Autowired
    private UserSettingMapper userSettingMapper;

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
        user.setKiosks(usersMapper.selectKioskList(userPk));
        user.setFloors(usersMapper.selectFloorList(userPk));
        return user;
    }

    public UserSettingDto selectUserSetting(long userId) {
        UserSettingDto dto = userSettingMapper.selectUserSetting(userId);

        if (dto != null) {

            List<FileEntity> fileList = commonMapper.selectFileList(connectableTypeSetting, dto.getId());

            for (FileEntity f : fileList) {

                if (f.getFileNumber() == 0) {//로고
                    dto.setLogoImg(f.getFullPath());
                }
                if (f.getFileNumber() == 1) {//매장/상품
                    dto.setStoreImg(f.getFullPath());
                }
                if (f.getFileNumber() == 2) {//교통정보
                    dto.setTrafficImg(f.getFullPath());
                }
                if (f.getFileNumber() == 3) {//편의시설
                    dto.setFacilitiesImg(f.getFullPath());
                }
                if (f.getFileNumber() == 4) {//관광지
                    dto.setAttractionsImg(f.getFullPath());
                }
            }
        }
        return dto;
    }

    private ResponsDto validateUserSetting(UserSettingEntity entity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(entity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!ElbigsUtil.isValidTime(entity.getOpenTime())) {
            res.addErrors("open_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(entity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.required-time"
                            , null, LocaleContextHolder.getLocale())});
        } else if (!ElbigsUtil.isValidTime(entity.getCloseTime())) {
            res.addErrors("close_time"
                    , new String[]{messageSource.getMessage("error.msg.invalid-time"
                            , null, LocaleContextHolder.getLocale())});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateUserSetting(HttpServletRequest request, long userPk) {


        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

        MultipartFile logoImgFile = multipartHttpServletRequest.getFile("logo_img[file]");
        MultipartFile storeImgFile = multipartHttpServletRequest.getFile("store_img[file]");
        MultipartFile trafficImgFile = multipartHttpServletRequest.getFile("traffic_img[file]");
        MultipartFile facilitiesImgFile = multipartHttpServletRequest.getFile("facilities_img[file]");
        MultipartFile attractionsImgFile = multipartHttpServletRequest.getFile("attractions_img[file]");

        long userSettingId = userSettingMapper.selectUserSettingId(userPk);


        boolean isNew = userSettingId > 0 ? false : true;
        Map<String, String[]> param = request.getParameterMap();

        String[] tels = new String[3];

        Map<String, Object> newMap = new HashMap<>();
        List<Long> delFiles = new ArrayList<>();

        boolean slogan1Bold = false;
        boolean slogan2Bold = false;
        boolean slogan3Bold = false;

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
//                System.out.println("key : " + k + ", val : " + v);
                if ("tel_1".equals(k)) {
                    tels[0] = v;
                }
                if ("tel_2".equals(k)) {
                    tels[1] = v;
                }
                if ("tel_3".equals(k)) {
                    tels[2] = v;
                }
                if ("slogan1_bold".equals(k)) {
                    slogan1Bold = "1".equals(v) ? true : false;
                }
                if ("slogan2_bold".equals(k)) {
                    slogan2Bold = "1".equals(v) ? true : false;
                }
                if ("slogan3_bold".equals(k)) {
                    slogan3Bold = "1".equals(v) ? true : false;
                }


                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {

                    if ("logo_img[isChange]".equals(k) && "1".contains(v) && logoImgFile == null) {
                        delFiles.add(0l);
                    }
                    if ("store_img[isChange]".equals(k) && "1".contains(v) && storeImgFile == null) {
                        delFiles.add(1l);
                    }
                    if ("traffic_img[isChange]".equals(k) && "1".contains(v) && trafficImgFile == null) {
                        delFiles.add(2l);
                    }
                    if ("facilities_img[isChange]".equals(k) && "1".contains(v) && facilitiesImgFile == null) {
                        delFiles.add(3l);
                    }
                    if ("attractions_img[isChange]".equals(k) && "1".contains(v) && attractionsImgFile == null) {
                        delFiles.add(4l);
                    }
                }
            }
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonElement jsonElement = gson.toJsonTree(newMap);
        UserSettingEntity entity = gson.fromJson(jsonElement, UserSettingEntity.class);

        entity.setSlogan1Bold(slogan1Bold);
        entity.setSlogan2Bold(slogan2Bold);
        entity.setSlogan3Bold(slogan3Bold);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateUserSetting(entity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(tels[0])) {
            validationRes.addErrors("tel_1", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone1(tels[0])) {
            validationRes.addErrors("tel_1"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_1"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[1])) {
            validationRes.addErrors("tel_2", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone2(tels[1])) {
            validationRes.addErrors("tel_2"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_2"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[2])) {
            validationRes.addErrors("tel_3", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone3(tels[2])) {
            validationRes.addErrors("tel_3"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_3"
                            , null, LocaleContextHolder.getLocale())});
        }

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        if (tels[0] != null && tels[1] != null && tels[2] != null) {
            entity.setTel(tels[0] + "-" + tels[1] + "-" + tels[2]);
        }
        entity.setUserId(userPk);
        entity.setId(userSettingId);

        /* eventEntity save */
        if (!isNew) {
            userSettingMapper.updateUserSetting(entity);
        } else {
            userSettingMapper.insertUserSetting(entity);
            userSettingId = entity.getId();
        }



        /* file save */
        for (Long n : delFiles) {
            commonMapper.deleteFiles(connectableTypeSetting, userSettingId, n);
        }

        if (logoImgFile != null) {
            saveFile(logoImgFile, 0, userSettingId);
        }
        if (storeImgFile != null) {
            saveFile(storeImgFile, 1, userSettingId);
        }
        if (trafficImgFile != null) {
            saveFile(trafficImgFile, 2, userSettingId);
        }
        if (facilitiesImgFile != null) {
            saveFile(facilitiesImgFile, 3, userSettingId);
        }
        if (attractionsImgFile != null) {
            saveFile(attractionsImgFile, 4, userSettingId);
        }


        validationRes.setSuccess(true);
        return validationRes;
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


        usersMapper.deleteFloor(userDto.getId());
        for (FloorEntity f : userDto.getFloors()) {
            f.setUserId(userDto.getId());
            usersMapper.insertFloor(f);
        }

        if (isNew) {

            for (KioskEntity k : userDto.getKiosks()) {
                k.setUserId(userDto.getId());
                k.setKey(ElbigsUtil.makeRandValue(6));
                usersMapper.insertKiosk(k);
            }


        } else {
            List<KioskEntity> kioskList = usersMapper.selectKioskList(userDto.getId());

            for (KioskEntity k : userDto.getKiosks()) {
                k.setUserId(userDto.getId());

                if (k.getId() > 0) {
                    usersMapper.updateKiosk(k);
                } else {
                    k.setKey(ElbigsUtil.makeRandValue(6));
                    usersMapper.insertKiosk(k);
                }
            }

            if (kioskList != null && kioskList.size() > 0) {
                for (KioskEntity oldData : kioskList) {

                    boolean delete = true;

                    for (KioskEntity newData : userDto.getKiosks()) {
                        if (newData.getId() == oldData.getId()) {
                            delete = false;
                            break;
                        }
                    }

                    if (delete) {
                        usersMapper.deleteKioskById(oldData.getId());
                    }
                }
            }
        }

        return res;
    }

    public void deleteUser(long id) {
        usersMapper.updateUserForDelete(id);
    }

    public void updatePassword(long userPk, String password){
        usersMapper.updatePassword(userPk, password);
    }

}

