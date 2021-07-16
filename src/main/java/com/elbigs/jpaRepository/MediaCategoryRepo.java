package com.elbigs.jpaRepository;

import com.elbigs.entity.MediaCategoryEntity;
import com.elbigs.entity.MediaLibEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaCategoryRepo extends JpaRepository<MediaCategoryEntity, Long> {

}
