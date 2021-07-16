package com.elbigs.jpaRepository;

import com.elbigs.entity.ShopEntity;
import com.elbigs.entity.TemplateCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateCategoryRepo extends JpaRepository<TemplateCategoryEntity, Long> {
    List<TemplateCategoryEntity> findByUpperCategoryIdOrderBySortNo(Long upperCategoryId);
}
