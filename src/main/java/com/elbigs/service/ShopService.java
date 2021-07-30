package com.elbigs.service;

import com.elbigs.dto.PagingParam;
import com.elbigs.dto.ResponseDto;
import com.elbigs.dto.ShopDeviceDto;
import com.elbigs.dto.ShopDto;
import com.elbigs.entity.CmsUserEntity;
import com.elbigs.entity.ShopDeviceEntity;
import com.elbigs.entity.ShopEntity;
import com.elbigs.entity.UserRolesEntity;
import com.elbigs.jpaRepository.CmsUserRepo;
import com.elbigs.jpaRepository.ShopDeviceRepo;
import com.elbigs.jpaRepository.ShopRepo;
import com.elbigs.jpaRepository.UserRolesRepo;
import com.elbigs.mybatisMapper.ShopDeviceMapper;
import com.elbigs.mybatisMapper.ShopMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShopService extends CommonService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShopRepo shopRepo;

    @Autowired
    private CmsUserRepo cmsUserRepo;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Autowired
    private ShopDeviceRepo shopDeviceRepo;

    @Autowired
    private DisplayService displayService;

    @Autowired
    private ShopDeviceMapper shopDeviceMapper;

    private ResponseDto validateDevice(List<ShopDeviceEntity> deviceEntitys) {
        ResponseDto res = new ResponseDto();
        String requireMsg = getMessage("error.msg.required");


        for (ShopDeviceEntity deviceEntity : deviceEntitys) {
            if (!StringUtils.hasLength(deviceEntity.getPanelId())) {
                res.addErrors("panelId", requireMsg);
            } else if (!StringUtils.hasLength(deviceEntity.getSettopId())) {
                res.addErrors("settopId", requireMsg);
            }
        }
        return res;

    }

    private ResponseDto validateShop(ShopEntity shopEntity, boolean isNew) {
        ResponseDto res = new ResponseDto();

        String requireMsg = getMessage("error.msg.required");

        // 필수 체크
        if (!StringUtils.hasLength(shopEntity.getName())) {
            res.addErrors("name", requireMsg);
        }

        // 사용자 정보 필수 체크
        if (shopEntity.getUser() == null) {
            res.addErrors("user.loginId", requireMsg);
//                res.addErrors("user.password", requireMsg);
        } else {

            // 로그인 아이디 체크
            if (!StringUtils.hasLength(shopEntity.getUser().getLoginId())) {
                res.addErrors("user.loginId", requireMsg);
            } else if (isNew) {

                CmsUserEntity user = cmsUserRepo.findByLoginId(shopEntity.getUser().getLoginId());
                if (user != null) {
                    res.addErrors("user.loginId", getMessage("error.msg.exist-user"));
                }
            }

            if (!StringUtils.hasLength(shopEntity.getUser().getName())) {
                res.addErrors("user.name", requireMsg);
            }

            boolean passwordExist = StringUtils.hasLength(shopEntity.getUser().getPassword());
            boolean passwordConfirmExist = StringUtils.hasLength(shopEntity.getUser().getPasswordConfirm());

            if (isNew && !passwordExist) {
                res.addErrors("user.password", requireMsg);
            }

            if (isNew && !passwordConfirmExist) {
                res.addErrors("user.passwordConfirm", requireMsg);
            }

            if (passwordExist && !passwordConfirmExist) {
                res.addErrors("user.passwordConfirm", requireMsg);
            }
            // 패스워드 체크
            else if (passwordExist && passwordConfirmExist) {
                logger.info("password length : " + shopEntity.getUser().getPassword().length());
                // 패스워드 길이체크
                if (shopEntity.getUser().getPassword().length() < 6) {
                    res.addErrors("user.password", getMessage("error.msg.change-password3"));
                } else if (!shopEntity.getUser().getPassword().equals(shopEntity.getUser().getPasswordConfirm())) {
                    // 패스워드 일치 체크
                    res.addErrors("user.password", getMessage("error.msg.change-password1"));
                    res.addErrors("user.passwordConfirm", getMessage("error.msg.change-password1"));
                }
            }

        }


        return res;
    }

    @Transactional
    public ResponseDto saveShop(ShopEntity entity) {

        boolean isNew = true;
        long shopId = 0;
        if (entity.getShopId() != null) {
            isNew = false;
        }

        // 유효성 체크 : 필수값, 형식, 길이 등
        ResponseDto validationRes = validateShop(entity, isNew);

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

//        validateDevice(entity.getShopDevices())


        // shop 등록
        shopRepo.save(entity);
        shopId = entity.getShopId();

        // 유저등록
        entity.getUser().setShopId(shopId);
        if (isNew) {
            entity.getUser().setPassword(BCrypt.hashpw(entity.getUser().getPassword(), BCrypt.gensalt()));
            entity.getUser().setStatus(1);
        }
        cmsUserRepo.save(entity.getUser());

        if (isNew) {
            UserRolesEntity userRolesEntity = new UserRolesEntity();
            userRolesEntity.setLoginId(entity.getUser().getLoginId());
            userRolesEntity.setRoles("ROLE_USER");
            userRolesRepo.save(userRolesEntity);
        }

        List<ShopDeviceEntity> shopDevices = entity.getShopDevices();

        // 매장 디바이스 저장
        displayService.saveShopDeviceAll(isNew, shopId, shopDevices);

        return validationRes;
    }

    public ShopEntity selectShop(long shopId) {
        ShopEntity shopEntity = shopRepo.findById(shopId);
        shopEntity.setUser(cmsUserRepo.findByShopIdAndStatus(shopId, 1));
//        shop.setShopDevices(shopDeviceMapper.selectShopDeviceList(shopId));
        return shopEntity;
    }

    public List<ShopEntity> selectShopList(PagingParam param) {
        return shopMapper.selectShopList(param);
    }

}
