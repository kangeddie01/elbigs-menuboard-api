package com.elbigs.service;

import com.elbigs.dto.QnaDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.QnaEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.QnaMapper;
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
public class QnaService {

    @Autowired
    private QnaMapper qnaMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-qna}")
    String connectableTypeQna;

    public List<QnaDto> selectQnaList(ShopReqDto req) {
        List<QnaDto> list = qnaMapper.selectQnaList(req);

        if (list != null) {
            for (QnaDto dto : list) {
                List<FileEntity> files = commonMapper.selectFileList(connectableTypeQna, dto.getId());
                if (files != null) {
                    dto.setFiles(files);
                }
            }
        }
        return list;
    }

    public QnaDto selectQna(long id) {

        QnaDto qnaDto = qnaMapper.selectQna(id);

        if (qnaDto != null) {
            List<FileEntity> files = commonMapper.selectFileList(connectableTypeQna, id);
            if (files != null) {
                qnaDto.setFiles(files);
            }
        }

        return qnaDto;
    }

    private ResponsDto validateQna(QnaEntity qnaEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(qnaEntity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(qnaEntity.getContent())) {
            res.addErrors("content", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateQna(HttpServletRequest request, long qnaId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = qnaId >= 0 ? false : true;

        Map<String, Object> newMap = new HashMap<>();

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
                System.out.println("key : " + k + ", val : " + v);

                if (!k.contains("[")) {
                    newMap.put(k, v);
                } else {
                }
            }
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonElement jsonElement = gson.toJsonTree(newMap);
        QnaEntity qnaEntity = gson.fromJson(jsonElement, QnaEntity.class);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateQna(qnaEntity);

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        qnaEntity.setId(qnaId);
        qnaEntity.setUserId(userPk);

        /* qnaEntity save */
        if (!isNew) {
            qnaMapper.updateQna(qnaEntity);
        } else {
            qnaMapper.insertQna(qnaEntity);
            qnaId = qnaEntity.getId();
        }

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("files[0]");

        /* file save */
        if (file != null) {
            String dir = DateUtil.getCurrDateStr("yyyyMMdd");
            String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 3);
            String uploadPath = dir + "/" + convertName + "." + ext;

            System.out.println("====================================================================");
            String uploadedPath = azureBlobAdapter.upload(file, uploadPath);

            if (uploadedPath != null) {
                String fullPath = azureStorageUrl + "/" + uploadedPath;

                FileEntity fileEntity = FileEntity.builder()
                        .fileNumber(0)
                        .connectableId(qnaId)
                        .connectableType(connectableTypeQna)
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

    public void deleteQna(long id) {
        qnaMapper.deleteQna(id);
    }
}

