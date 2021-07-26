package com.elbigs.service;

import com.elbigs.dto.MediaLibDto;
import com.elbigs.entity.MediaCategoryEntity;
import com.elbigs.entity.MediaLibEntity;
import com.elbigs.jpaRepository.MediaCategoryRepo;
import com.elbigs.jpaRepository.MediaLibRepo;
import com.elbigs.mybatisMapper.MediaMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

@Service
public class MediaService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MediaCategoryRepo mediaCategoryRepo;

    @Autowired
    private MediaMapper mediaMapper;

    @Autowired
    private MediaLibRepo mediaLibRepo;

    /**
     * 미디어 카테고리 조회
     *
     * @return
     */
    public List<MediaCategoryEntity> selectMediaCategoryList(MediaCategoryEntity param) {

        if (param.getCategoryType() != null) {
            return mediaCategoryRepo.findByCategoryTypeAndShopIdOrderBySortNo(param.getCategoryType(), param.getShopId());
        } else {
            return mediaCategoryRepo.findByShopIdOrderBySortNo(param.getShopId());
        }
    }

    /**
     * 미디어 목록 조회
     *
     * @param param i.이미지, v.동영상, b.뱃지
     * @return
     */
    public List<MediaLibDto> selectMediaLibList(MediaLibEntity param) {
        return mediaMapper.selectMediaLibList(param);
    }


    public MediaLibEntity insertMediaLib(MultipartFile file, MediaLibEntity entity) {

        String orgFileName = file.getOriginalFilename();
        String ext = orgFileName.substring(orgFileName.length() - 3, orgFileName.length());

        // 이미지 cloud upload
        String dir = DateUtil.getCurrDateStr("yyyyMMdd");
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String uploadPath = dir + "/" + convertName + "." + ext;
//        System.out.println("uploadPath : " + uploadPath);


        try {
            BufferedImage bi = ImageIO.read(file.getInputStream());
            entity.setResolution(bi.getWidth() + "x" + bi.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

        azureBlobAdapter.upload(file, uploadPath);

        entity.setSize(file.getSize());
        entity.setOrginFilename(orgFileName);
        entity.setMediaPath(uploadPath);

        mediaLibRepo.save(entity);

        return mediaLibRepo.findById(entity.getMediaLibId()).get();
    }


    /**
     * 미디어 카테고리 등록/수정
     *
     * @param entity
     */
    public void updateMediaCategory(MediaCategoryEntity entity) {
        mediaCategoryRepo.save(entity);
    }

    /**
     * 미디어 카테고리 삭제
     * @param mediaCategoryId
     */
    public void deleteMediaCategory(Long mediaCategoryId) {
        mediaCategoryRepo.deleteById(mediaCategoryId);
    }

    /**
     * 미디어 카테고리 전체 저장 ( 소팅 변경 )
     *
     * @param list
     */
    public void updateMediaCategoryAll(List<MediaCategoryEntity> list) {
        mediaCategoryRepo.saveAll(list);
    }
}
