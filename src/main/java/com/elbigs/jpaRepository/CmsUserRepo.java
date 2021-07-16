package com.elbigs.jpaRepository;

import com.elbigs.entity.CmsUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmsUserRepo extends JpaRepository<CmsUserEntity, Long> {
    CmsUserEntity findByLoginId(String loginId);
    CmsUserEntity findByShopId(Long shopId);
}
