package com.elbigs.jpaRepository;

import com.elbigs.entity.ShopDeviceEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDeviceRepo extends CrudRepository<ShopDeviceEntity, Long> {
    void removeAllByShopId(long shopId);

    List<ShopDeviceEntity> findByShopIdOrderBySortNoAsc(long shopId);

    List<ShopDeviceEntity> findByShopId(long shopId);

}
