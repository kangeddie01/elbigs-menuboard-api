package com.elbigs.service;

import com.elbigs.dto.MessageDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.MessageEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.MessageMapper;
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
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-message}")
    String connectableTypeMessage;

    public List<MessageDto> selectMessageList(ShopReqDto req) {
        List<MessageDto> list = messageMapper.selectMessageList(req);

        if (list != null) {
            for (MessageDto dto : list) {
                List<FileEntity> files = commonMapper.selectFileList(connectableTypeMessage, dto.getId());
                if (files != null) {
                    dto.setFiles(files);
                }
            }
        }
        return list;
    }

    public MessageDto selectMessage(long id) {

        MessageDto messageDto = messageMapper.selectMessage(id);

        if (messageDto != null) {
            List<FileEntity> files = commonMapper.selectFileList(connectableTypeMessage, id);
            if (files != null) {
                messageDto.setFiles(files);
            }
        }

        return messageDto;
    }

    private ResponsDto validateMessage(MessageEntity messageEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(messageEntity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(messageEntity.getContent())) {
            res.addErrors("content", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateMessage(HttpServletRequest request, long messageId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = messageId >= 0 ? false : true;

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
                System.out.println("key : " + k + ", val : " + v);

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
        MessageEntity messageEntity = gson.fromJson(jsonElement, MessageEntity.class);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateMessage(messageEntity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        messageEntity.setId(messageId);

        /* messageEntity save */
        if (!isNew) {
            messageMapper.updateMessage(messageEntity);
        } else {
            messageMapper.insertMessage(messageEntity);
            messageId = messageEntity.getId();
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
            commonMapper.deleteFiles(connectableTypeMessage, messageId, fileMap.get(f));
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
                        .connectableId(messageId)
                        .connectableType(connectableTypeMessage)
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

    public void deleteMessage(long id) {
        messageMapper.deleteMessage(id);
    }
}

