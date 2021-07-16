package com.elbigs.jpaRepository;

import com.elbigs.entity.HtmlTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HtmlTemplateRepo extends JpaRepository<HtmlTemplateEntity, Long> {
    List<HtmlTemplateEntity> findByRecommendYn(String recommendYn);
    List<HtmlTemplateEntity> findByRecommendYnAndTemplateCategoryId(String recommendYn, Long templateCategoryId);
    List<HtmlTemplateEntity> findByTemplateCategoryIdAndScreenRatio(Long templateCategoryId, String screenRatio);
    List<HtmlTemplateEntity> findByScreenRatio(String screenRatio);
}
