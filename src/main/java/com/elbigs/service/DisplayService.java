package com.elbigs.service;

import com.elbigs.dto.FileDto;
import com.elbigs.dto.MediaLibDto;
import com.elbigs.dto.ShopDeviceDto;
import com.elbigs.dto.ShopDisaplyDto;
import com.elbigs.entity.*;
import com.elbigs.jpaRepository.*;
import com.elbigs.mybatisMapper.DisplayMapper;
import com.elbigs.mybatisMapper.ShopDeviceMapper;
import com.elbigs.util.*;
import com.sun.jdi.LongValue;
import gui.ava.html.image.generator.HtmlImageGenerator;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisplayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private ShopDisplayRepo shopDisplayRepo;

    @Autowired
    private ShopDeviceRepo shopDeviceRepo;

    @Autowired
    private ShopDeviceMapper shopDeviceMapper;

    @Autowired
    private MediaLibRepo mediaLibRepo;

    @Autowired
    private MediaCategoryRepo mediaCategoryRepo;

    @Autowired
    private DisplayMapper displayMapper;

    @Autowired
    private HtmlTemplateRepo htmlTemplateRepo;

    @Autowired
    private TemplateCategoryRepo templateCategoryRepo;


    @Value("${html.display.path}")
    public String DISPLAY_PATH;

    @Value("${html.template.path}")
    public String TEMPLATE_PATH;

    @Value("${spring.url.base}")
    public String SERVER_URL;

    @Value("${pdfcrowd.auth.user}")
    public String PDFCROWD_USER_NAME;

    @Autowired
    private HtmlToImage htmlToImage;


    /**
     * create preview image & upload azure
     *
     * @param htmlUrl
     * @param targetDir
     * @return uploaded path ( azure )
     */
    private String makePreviewAndUpload(String htmlUrl, String targetDir) {


        String fileExt = "jpg";
        String localSavePath = targetDir + File.separator + "priview." + fileExt;
        boolean isSuccess = htmlToImage.convertToImage(htmlUrl, localSavePath, fileExt);

        String dir = DateUtil.getCurrDateStr("yyyyMMdd");
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String uploadPath = dir + "/" + convertName + "." + fileExt;

        File file = new File(localSavePath);

        if (isSuccess && file.exists()) {
            azureBlobAdapter.uploadLocalFile(uploadPath, localSavePath);
            return uploadPath;
        } else {
            return null;
        }
    }


    private boolean saveHtml(String html, long shopDisplayId) {

//        String templateName = ElbigsUtil.makeRandAlpabet(10, true); //
        String savePathDir = DISPLAY_PATH + File.separator + "display_" + shopDisplayId;
        String saveHtmlPath = DISPLAY_PATH + File.separator + "display_" + shopDisplayId + File.separator + "display.html";

        logger.info("dir : " + savePathDir);
        logger.info("htmlPath : " + saveHtmlPath);

        File dir = new File(savePathDir);
        File file = new File(saveHtmlPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(html);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateShopDisplayTitle(Long shopDisplayId, String displayName){

        ShopDisplayEntity entity = shopDisplayRepo.findById(shopDisplayId).get();
        entity.setDisplayName(displayName);
        shopDisplayRepo.save(entity);
    }

    @Transactional
    public ShopDisplayEntity saveContent(ShopDisaplyDto dto) throws IOException {

        boolean isNew = dto.getShopDisplayId() == null;

        ShopDisplayEntity shopDisplay = new ShopDisplayEntity();
        shopDisplay.setShopDisplayId(dto.getShopDisplayId());
//        shopDisplay.setDisplayHtml(dto.getDisplayHtml());
        shopDisplay.setShopId(dto.getShopId());
        shopDisplay.setDisplayName(dto.getDisplayName());
        shopDisplay.setScreenRatio(dto.getScreenRatio());
        shopDisplayRepo.save(shopDisplay);


        logger.info("step 1 : html save !!");

        // html 저장
        boolean success = saveHtml(dto.getDisplayHtml(), shopDisplay.getShopDisplayId());

        String displayPath = DISPLAY_PATH + File.separator + "display_" + shopDisplay.getShopDisplayId();

        logger.info("step 2 : static copy !! [before success? " + success + "]");

        // static 디렉토리 카피 ( template_{htmlTemplateId} => display_{shopDisplayId}
        if (isNew) {
            String destStaticDir = displayPath + File.separator + "static";

//            File destFile = new File(destStaticDir);
//            if (!destFile.exists()) {
//                if (destFile.mkdirs()) {
//                    logger.info("isExists Static Dir ? " + destFile.exists());
//                } else {
//                    logger.info("fail dir create");
//                }
//            }

            String sourceStaticDir = TEMPLATE_PATH + File.separator + "template_" + dto.getHtmlTemplateId().toString() + File.separator + "static";
            logger.info("template static path : " + sourceStaticDir);
            FileUtil.copyDir(new File(sourceStaticDir), new File(destStaticDir));
        }

        logger.info("step 3 : zipping !!");
        // zip 압축
        String localZipPath = displayPath + File.separator + "display_.zip";
        ZipUtils.zipFolder(displayPath, localZipPath);

        logger.info("step 4 : upload zip file !!");
        // 쿨라우드 업로드
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String zipUploadPath = DateUtil.getCurrDateStr("yyyyMMdd") + "/" + convertName + ".zip";
        azureBlobAdapter.uploadLocalFile(zipUploadPath, localZipPath);

        logger.info("step 5 : delete zip file !!");
        // zip파일 삭제
        File file = new File(localZipPath);
        if (file.exists()) {
            file.delete();
        }

        logger.info("step 6: preview image create and upload !!");


        // 프리뷰 이미지 cloud upload
        String htmlSaveUrl = SERVER_URL + "/displays/display_" + shopDisplay.getShopDisplayId() + "/display.html";

        String preViewUploadPath = makePreviewAndUpload(htmlSaveUrl, displayPath);

        if (preViewUploadPath == null) {

            try {
                Thread.sleep(2000);// 2 sec
            } catch (Exception e) {

            }
            preViewUploadPath = makePreviewAndUpload(htmlSaveUrl, displayPath);
        }
        // 경로 저장
        shopDisplay.setDownloadPath(zipUploadPath);
        if (preViewUploadPath != null) {
            shopDisplay.setPreviewImagePath(preViewUploadPath);
        }
        shopDisplayRepo.save(shopDisplay);

        logger.info("new display id : " + shopDisplay.getShopDisplayId());

        return shopDisplay;

    }


    /**
     * shop_device 에 shop_display_id 매핑 ( list )
     *
     * @param entity
     */
    public void saveShopDeviceDisplays(List<ShopDeviceEntity> entities) {
        for (ShopDeviceEntity entity : entities) {
            shopDeviceMapper.updateShopDeviceDisplay(entity);
        }
    }

    /**
     * shop_device 에 shop_display_id 매핑 ( 단건 )
     *
     * @param entity
     */
    public void saveShopDeviceDisplay(ShopDeviceEntity entity) {
//        shopDeviceMapper.updateShopDeviceDisplay(entity);
        shopDeviceRepo.save(entity);
    }

    /**
     * 패널에 화면 적용
     *
     * @param entity
     */
    public void updateDeviceDisplayMapping(ShopDeviceEntity entity) {
        shopDeviceMapper.updateShopDeviceDisplay(entity);
    }

    @Transactional
    public void updateDeviceDisplayMappings(List<ShopDeviceEntity> list) {

        for(ShopDeviceEntity device : list){
            shopDeviceMapper.updateShopDeviceDisplay(device);
        }

    }

    /**
     * 상점의 디바이스 정보를 갱신한다 ( delete and insert )
     *
     * @param shopId
     * @param entitys
     */
    public void saveShopDeviceAll(boolean isNew, long shopId, List<ShopDeviceEntity> tobeDevices) {

        List<Long> delList = null;
        boolean exists = false;

        if (!isNew) {
            List<ShopDeviceEntity> asisDevices = shopDeviceRepo.findByShopId(shopId);

            for (ShopDeviceEntity asisDevice : asisDevices) {
                for (ShopDeviceEntity tobeDevice : tobeDevices) {

                    if (asisDevice.getShopDeviceId() == tobeDevice.getShopDeviceId()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    shopDeviceRepo.deleteById(asisDevice.getShopDeviceId());
                }
                exists = false;
            }
        }
        for (ShopDeviceEntity tobeDevice : tobeDevices) {
            tobeDevice.setShopId(shopId);
            shopDeviceRepo.save(tobeDevice);
        }
    }

    public ShopDisplayEntity selectShopDisplay(long id) {
        return shopDisplayRepo.findById(id);
    }

    /**
     * 상점의 내 화면 목록 조회
     *
     * @param shopId
     * @param screenRatio x,y
     * @return
     */
    public List<ShopDisplayEntity> selectShopDisplayList(Long shopId, String screenRatio) {
        return shopDisplayRepo.findByShopIdAndScreenRatioOrderByUpdatedAtDescCreatedAtDesc(shopId, screenRatio);
    }

    /**
     * 상점의 패널(디바이스) 목록 조회
     *
     * @param shopId
     * @return
     */
    public List<ShopDeviceDto> selectShopDeviceList(Long shopId) {
//        shopDeviceRepo.findByShopIdOrderBySortNoAsc(shopId);
        return shopDeviceMapper.selectShopDeviceList(shopId);
    }


    /**
     * 템플릿 카테고리 목록 조회 ( 계층구조 )
     *
     * @return
     */
    public List<TemplateCategoryEntity> selectTemplateCategoryList() {
        return displayMapper.selectTemplateCategoryList();
    }

    /**
     * 상위 번호로 하위 카테고리 목록 조회
     *
     * @param upperCategoryId
     * @return
     */
    public List<TemplateCategoryEntity> selectTemplateCategoryListByUpper(Long upperCategoryId) {
        return templateCategoryRepo.findByUpperCategoryIdOrderBySortNo(upperCategoryId);
    }


    /**
     * 추천 템플릿 조회
     *
     * @param templateCategoryId
     * @return
     */
    public List<HtmlTemplateEntity> selectRecommendTemplateList(Long templateCategoryId) {
        if (templateCategoryId == -1) {
            return htmlTemplateRepo.findByRecommendYn("Y");
        } else {
            return htmlTemplateRepo.findByRecommendYnAndTemplateCategoryId("Y", templateCategoryId);
        }
    }

    public List<HtmlTemplateEntity> selectTemplateList2(Long templateCategoryId, String screenRatio, String recommendYn) {
        HtmlTemplateEntity param = new HtmlTemplateEntity();
        param.setRecommendYn(recommendYn);
        param.setScreenRatio(screenRatio);
        param.setTemplateCategoryId(templateCategoryId);
        return displayMapper.selectHtmlTemplateList(param);
    }
    /**
     * 일반 템플릿 조회
     *
     * @param templateCategoryId
     * @param screenRatio
     * @return
     */
    public List<HtmlTemplateEntity> selectTemplateList(Long templateCategoryId, String screenRatio) {
        if (templateCategoryId == -1) {
            return htmlTemplateRepo.findByScreenRatio(screenRatio);
        } else {
            return htmlTemplateRepo.findByTemplateCategoryIdAndScreenRatio(templateCategoryId, screenRatio);
        }

    }

    public void deleteShopDisplay(Long shopDisplayId) {
        shopDisplayRepo.deleteById(shopDisplayId);
    }


    /**
     * 템플릿 업로드
     *
     * @param file
     * @param entity
     * @return
     * @throws IOException
     */
    @Transactional
    public boolean uploadTemplate(MultipartFile file, HtmlTemplateEntity entity) throws IOException, ZipException, Exception {

        String orgFileName = file.getOriginalFilename();
        String ext = orgFileName.substring(orgFileName.length() - 3, orgFileName.length());
        String dir = DateUtil.getCurrDateStr("yyyyMMdd");
        String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
        String azureUploadPath = dir + "/" + convertName + "." + ext;

        // 1. htmlTemplateId 생성
        logger.info("step 1 : htmlTemplateId 생성");
        entity.setTemplateCategoryId(Long.valueOf(1));
        entity.setTemplateZipPath("zip_path");
        htmlTemplateRepo.save(entity);

        long htmlTemplateId = entity.getHtmlTemplateId();

        // 2. zip 파일 저장
        String targetPath = TEMPLATE_PATH + File.separator + "template_" + htmlTemplateId;
        String zipFileName = "template.zip";
        logger.info("step 2 : zip 파일 저장(" + targetPath + File.separator + zipFileName + ")");
        FileUtil.writeFile(file.getBytes(), zipFileName, targetPath);

        // 3. 압축 해제
        logger.info("step 3 : 압축 해제");
        ZipUtils.unzip(targetPath + File.separator + zipFileName, targetPath);

        // 4. preview 이미지 생성
        logger.info("step 4 : preview 이미지 생성");
        String htmlSaveUrl = SERVER_URL + "/template/template_" + htmlTemplateId + "/template.html";

        String preViewUploadPath = makePreviewAndUpload(htmlSaveUrl, targetPath);

        if (preViewUploadPath == null) {

            try {
                Thread.sleep(2000);// 2 sec
            } catch (Exception e) {

            }
            preViewUploadPath = makePreviewAndUpload(htmlSaveUrl, targetPath);
        }

        logger.info("preview image path : " + preViewUploadPath);
        if(preViewUploadPath==null){
            throw new Exception("preview image fail");
        }
        entity.setPreviewImagePath(preViewUploadPath);

        // 5. db에 정보 저장
        logger.info("step 5 : db에 정보 저장");
        htmlTemplateRepo.save(entity);

        return true;
    }

    /**
     * 제작된 화면을 복사하여 생성한다.
     *
     * @param shopId
     * @param shopDisplayId
     * @return
     */
    @Transactional
    public boolean copyDisplay(Long shopId, Long shopDisplayId) throws IOException {

        // 1. insert shop_display
        ShopDisplayEntity beforeDisplay = shopDisplayRepo.findById(shopDisplayId).get();


        ShopDisplayEntity newDisplay = new ShopDisplayEntity();
        newDisplay.setDisplayName(beforeDisplay.getDisplayName() + "_복사본");
        newDisplay.setScreenRatio(beforeDisplay.getScreenRatio());
        newDisplay.setDownloadPath(beforeDisplay.getDownloadPath());
        newDisplay.setShopId(shopId);
        newDisplay.setPreviewImagePath(beforeDisplay.getPreviewImagePath());
        newDisplay.setStatus(beforeDisplay.getStatus());

        shopDisplayRepo.save(newDisplay);

        Long newShopDisplayId = newDisplay.getShopDisplayId();
        logger.info("before shopDisplayId : " + shopDisplayId);
        logger.info("new shopDisplayId : " + newShopDisplayId);

        // 2. html 디렉토리 복사
        String sourceDir = DISPLAY_PATH + File.separator + "display_" + shopDisplayId;
        String targetDir = DISPLAY_PATH + File.separator + "display_" + newShopDisplayId;
        logger.info("sourceDir : " + sourceDir);
        logger.info("targetDir : " + targetDir);
        FileUtil.copyDir(new File(sourceDir), new File(targetDir));

        return true;
    }
}
