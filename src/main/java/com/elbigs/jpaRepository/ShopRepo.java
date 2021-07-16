package com.elbigs.jpaRepository;

import com.elbigs.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends JpaRepository<ShopEntity, Long> {
    ShopEntity findById(long id);
}
