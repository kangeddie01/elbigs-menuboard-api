package com.elbigs.jpaRepository;

import com.elbigs.entity.MediaLibEntity;
import com.elbigs.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaLibRepo extends JpaRepository<MediaLibEntity, Long> {
    List<MediaLibEntity> findByMediaCategoryIdAndMediaTypeOrderByUpdatedAtDescCreatedAtAsc(long mediaCategoryId, String mediaType);
    List<MediaLibEntity> findByMediaTypeOrderByUpdatedAtDescCreatedAtAsc(String mediaType);
}
