package com.elbigs.service;

import com.elbigs.entity.menuboard.ShopDeviceEntity;
import com.elbigs.entity.menuboard.ShopEntity;
import com.elbigs.jpaRepository.ShopRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ShopService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShopRepo shopRepo;
    @Autowired
    private DisplayService displayService;

    @Transactional
    public void saveShop(ShopEntity entity) {

        boolean isNew = true;
        long shopId = 0;
        if (entity.getShopId() != null) {
            isNew = false;
        }
        shopRepo.save(entity);
        shopId = entity.getShopId();

        List<ShopDeviceEntity> shopDevices = entity.getShopDevices();

        // 매장 디바이스 저장
        displayService.saveShopDeviceAll(isNew, shopId, shopDevices);
    }

    public ShopEntity selectShop(long shopId) {
        return shopRepo.findById(shopId);
    }

    public Iterable<ShopEntity> selectShopList() {
        return shopRepo.findAll();
    }
}
