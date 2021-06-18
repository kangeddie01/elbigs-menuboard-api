package com.elbigs.jpaRepository;

import com.elbigs.entity.menuboard.ShopDisplayEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDisplayRepo extends CrudRepository<ShopDisplayEntity, Long> {

    List<ShopDisplayEntity> findByShopId(long shopId);

    ShopDisplayEntity findById(long id);
}
