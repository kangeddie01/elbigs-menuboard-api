package com.elbigs.service;

import com.elbigs.dto.NoticeDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.NoticeEntity;
import com.elbigs.entity.FileEntity;
import com.elbigs.mybatisMapper.CommonMapper;
import com.elbigs.mybatisMapper.NoticeMapper;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-notice}")
    String connectableTypeNotice;

    public List<NoticeDto> selectNoticeList(ShopReqDto req) {
        List<NoticeDto> list = noticeMapper.selectNoticeList(req);

        if (list != null) {
            for (NoticeDto dto : list) {
                List<FileEntity> files = commonMapper.selectFileList(connectableTypeNotice, dto.getId());
                if (files != null) {
                    dto.setFiles(files);
                }
            }
        }
        return list;
    }

    public NoticeDto selectNotice(long id) {

        NoticeDto noticeDto = noticeMapper.selectNotice(id);

        if (noticeDto != null) {
            List<FileEntity> files = commonMapper.selectFileList(connectableTypeNotice, id);
            if (files != null) {
                noticeDto.setFiles(files);
            }
        }

        return noticeDto;
    }

    private ResponsDto validateNotice(NoticeEntity noticeEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(noticeEntity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(noticeEntity.getContent())) {
            res.addErrors("content", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateNotice(HttpServletRequest request, long noticeId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = noticeId >= 0 ? false : true;

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();
        boolean noticeStatus = false;

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
//                System.out.println("key : " + k + ", val : " + v);
                if ("notice_status".equals(k)) {
                    noticeStatus = "1".equals(v) ? true : false;
                }

                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {
                    if (k.contains("files")) {
                        if (k.contains("id")) {
                            fileMap.put(k.substring(0, 8), Long.parseLong(v));
                        }
                        if (v != null && v.contains("delete")) {
                            delFiles.add(k.substring(0, 8));
                        }
                        if ("change".equals(v)) {
                            changeFiles.add(k.substring(0, 8));
                        }
                    }
                }
            }
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonElement jsonElement = gson.toJsonTree(newMap);
        NoticeEntity noticeEntity = gson.fromJson(jsonElement, NoticeEntity.class);

        noticeEntity.setNoticeStatus(noticeStatus);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateNotice(noticeEntity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        noticeEntity.setId(noticeId);

        /* noticeEntity save */
        if (!isNew) {
            noticeMapper.updateNotice(noticeEntity);
        } else {
            noticeMapper.insertNotice(noticeEntity);
            noticeId = noticeEntity.getId();
        }

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile[] files = new MultipartFile[5];

        files[0] = multipartHttpServletRequest.getFile("files[0]");
        files[1] = multipartHttpServletRequest.getFile("files[1]");
        files[2] = multipartHttpServletRequest.getFile("files[2]");
        files[3] = multipartHttpServletRequest.getFile("files[3]");
        files[4] = multipartHttpServletRequest.getFile("files[4]");

        /* file save */
        for (String f : delFiles) {
            commonMapper.deleteFiles(connectableTypeNotice, noticeId, fileMap.get(f));
        }

        for (String f : changeFiles) {

            long changeId = fileMap.get(f);
            MultipartFile file = files[(int) changeId];
            String dir = DateUtil.getCurrDateStr("yyyyMMdd");
            String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 3);
            String uploadPath = dir + "/" + convertName + "." + ext;

            System.out.println("====================================================================");
            String uploadedPath = azureBlobAdapter.upload(file, uploadPath);

            if (uploadedPath != null) {
                String fullPath = azureStorageUrl + "/" + uploadedPath;

                FileEntity fileEntity = FileEntity.builder()
                        .fileNumber(fileMap.get(f))
                        .connectableId(noticeId)
                        .connectableType(connectableTypeNotice)
                        .originName(file.getOriginalFilename())
                        .convertName(convertName + "." + ext)
                        .fullPath(fullPath)
                        .type("file")
                        .mimeType(file.getContentType())
                        .size(String.valueOf(file.getSize()))
                        .build();

                commonMapper.insertFiles(fileEntity);
            }
        }
        validationRes.setSuccess(true);
        return validationRes;
    }

    public void deleteNotice(long id) {
        noticeMapper.deleteNotice(id);
    }
}

