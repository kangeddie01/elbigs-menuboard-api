package com.elbigs.service;

import com.elbigs.dto.ShopDisaplyDto;
import com.elbigs.entity.menuboard.ShopDeviceEntity;
import com.elbigs.entity.menuboard.ShopDisplayEntity;
import com.elbigs.jpaRepository.ShopDeviceRepo;
import com.elbigs.jpaRepository.ShopDisplayRepo;
import com.elbigs.mybatisMapper.ShopDeviceMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AzureBlobAdapter AzureBlobAdapter;

    @Autowired
    private ShopDisplayRepo shopDisplayRepo;

    @Autowired
    private ShopDeviceRepo shopDeviceRepo;

    @Autowired
    private ShopDeviceMapper shopDeviceMapper;

    public void saveContent(ShopDisaplyDto dto) {

        // 프리뷰 이미지 cloud upload
        String dir = DateUtil.getCurrDateStr("yyyyMMdd");
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String uploadPath = dir + "/" + convertName + ".jpg";
        System.out.println("uploadPath : " + uploadPath);
        AzureBlobAdapter.upload(dto.getPreviewDataUrl(), uploadPath);

        dto.setPreviewImagePath(uploadPath);

        ShopDisplayEntity shopDisplay = new ShopDisplayEntity();

        shopDisplay.setShopDisplayId(dto.getShopDisplayId());
        shopDisplay.setDisplayHtml(dto.getDisplayHtml());
        shopDisplay.setPreviewImagePath(uploadPath);
        shopDisplay.setShopId(dto.getShopId());
        shopDisplay.setDisplayName(dto.getDisplayName());
        shopDisplayRepo.save(shopDisplay);

        logger.info("new display id : " + shopDisplay.getShopDisplayId());

    }

    /**
     * shop_device 에 shop_display_id 매핑 ( list )
     *
     * @param entity
     */
    public void saveShopDeviceDisplays(List<ShopDeviceEntity> entities) {
        for (ShopDeviceEntity entity : entities) {
            shopDeviceMapper.updateShopDeviceDisplay(entity);
        }
    }

    /**
     * shop_device 에 shop_display_id 매핑 ( 단건 )
     *
     * @param entity
     */
    public void saveShopDeviceDisplay(ShopDeviceEntity entity) {
//        shopDeviceMapper.updateShopDeviceDisplay(entity);
        shopDeviceRepo.save(entity);
    }

    /**
     * 상점의 디바이스 정보를 갱신한다 ( delete and insert )
     *
     * @param shopId
     * @param entitys
     */
    public void saveShopDeviceAll(boolean isNew, long shopId, List<ShopDeviceEntity> tobeDevices) {

        List<Long> delList = null;
        boolean exists = false;

        if (!isNew) {
            List<ShopDeviceEntity> asisDevices = shopDeviceRepo.findByShopId(shopId);

            for (ShopDeviceEntity asisDevice : asisDevices) {
                for (ShopDeviceEntity tobeDevice : tobeDevices) {

                    if (asisDevice.getShopDeviceId() == tobeDevice.getShopDeviceId()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    shopDeviceRepo.deleteById(asisDevice.getShopDeviceId());
                }
                exists = false;
            }
        }
        for (ShopDeviceEntity tobeDevice : tobeDevices) {
            tobeDevice.setShopId(shopId);
            shopDeviceRepo.save(tobeDevice);
        }
    }

    public ShopDisplayEntity selectShopDisplay(long id) {
        return shopDisplayRepo.findById(id);
    }

    public List<ShopDisplayEntity> selectShopDisplayList(long shopId) {
        return shopDisplayRepo.findByShopId(shopId);
    }
}
