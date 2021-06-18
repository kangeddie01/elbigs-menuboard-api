package com.elbigs.jpaRepository;

import com.elbigs.entity.menuboard.ShopDeviceEntity;
import com.elbigs.entity.menuboard.ShopDisplayEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDeviceRepo extends CrudRepository<ShopDeviceEntity, Long> {
    void removeAllByShopId(long shopId);

    List<ShopDeviceEntity> findByShopId(long shopId);

}
