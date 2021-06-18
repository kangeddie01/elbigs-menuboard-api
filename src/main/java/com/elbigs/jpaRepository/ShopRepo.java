package com.elbigs.jpaRepository;

import com.elbigs.entity.menuboard.ShopEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends CrudRepository<ShopEntity, Long> {
    ShopEntity findById(long id);
}
