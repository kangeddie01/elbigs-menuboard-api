package com.elbigs.service;

import com.elbigs.dto.EventDto;
import com.elbigs.dto.LineNoticeDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.FileEntity;
import com.elbigs.entity.LineNoticeEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.LineNoticeMapper;
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
public class LineNoticeService {

    @Autowired
    private LineNoticeMapper lineNoticeMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-lineNotice}")
    String connectableTypeLineNotice;


    public List<LineNoticeDto> selectLineNoticeList(ShopReqDto req) {

        List<LineNoticeDto> list = lineNoticeMapper.selectLineNoticeList(req);

        if (list != null) {
            for (LineNoticeDto dto : list) {
                dto.setFiles(commonMapper.selectFileList(connectableTypeLineNotice, dto.getId()));
            }
        }

        return list;
    }


    public EventDto selectLineNotice(long id) {

        EventDto eventDto = lineNoticeMapper.selectLineNotice(id);

        return eventDto;
    }

    private ResponsDto validateLineNotice(LineNoticeEntity entity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(entity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }

        if (!entity.isPeriodStatus() && entity.getStartDate() == null) {
            res.addErrors("start_date", new String[]{requireMsg});
        }
        if (!entity.isPeriodStatus() && entity.getEndDate() == null) {
            res.addErrors("end_date", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateLineNotice(HttpServletRequest request, long lineNoticeId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = lineNoticeId >= 0 ? false : true;
        String[] tels = new String[3];

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        boolean periodStatus = false;
        boolean status = false;

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
                System.out.println("key : " + k + ", val : " + v);
                if ("period_status".equals(k)) {
                    periodStatus = "1".equals(v) ? true : false;
                }
                if ("status".equals(k)) {
                    status = "1".equals(v) ? true : false;
                }
                if (!k.contains("[")) {
                    newMap.put(k, v);
                }
            }
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonElement jsonElement = gson.toJsonTree(newMap);
        LineNoticeEntity entity = gson.fromJson(jsonElement, LineNoticeEntity.class);

        entity.setPeriodStatus(periodStatus);
        entity.setStatus(status);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateLineNotice(entity);

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        entity.setId(lineNoticeId);
        entity.setUserId(userPk);

        /* eventEntity save */
        if (!isNew) {
            lineNoticeMapper.updateLineNotice(entity);
        } else {
            lineNoticeMapper.insertLineNotice(entity);
            lineNoticeId = entity.getId();
        }

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multipartHttpServletRequest.getFile("files[0]");

        /* file save */
        if (file != null) {
            commonMapper.deleteFiles(connectableTypeLineNotice, lineNoticeId, 0);

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
                        .connectableId(lineNoticeId)
                        .connectableType(connectableTypeLineNotice)
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

    public void deleteLineNotice(long id) {
        lineNoticeMapper.deleteLineNotice(id);
    }
}

