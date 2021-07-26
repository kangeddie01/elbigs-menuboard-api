package com.elbigs.jpaRepository;

import com.elbigs.entity.MediaCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaCategoryRepo extends JpaRepository<MediaCategoryEntity, Long> {

    List<MediaCategoryEntity> findByCategoryTypeAndShopIdOrderBySortNo(String categoryType, Long shopId);

    List<MediaCategoryEntity> findByShopIdOrderBySortNo(Long shopId);

}
