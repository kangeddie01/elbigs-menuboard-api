package com.elbigs.service;

import com.elbigs.dto.EventDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.EventEntity;
import com.elbigs.entity.FileEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.EventMapper;
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
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Autowired
    private MessageSource messageSource;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @Value("${file.connectable-type-event}")
    String connectableTypeEvent;

    public List<EventDto> selectEventList(ShopReqDto req) {
        return eventMapper.selectEventList(req);
    }

    public List<EventDto> selectEventList2(ShopReqDto req) {
        List<EventDto> eventList = eventMapper.selectEventList(req);
        if (eventList == null) {
            return null;
        }
        for (EventDto eventDto : eventList) {
            eventDto.setFiles(commonMapper.selectFileList(connectableTypeEvent, eventDto.getId()));
        }
        return eventList;
    }

    public EventDto selectEvent(long id) {

        EventDto eventDto = eventMapper.selectEvent(id);

        if (eventDto != null) {
            eventDto.setFiles(commonMapper.selectFileList(connectableTypeEvent, id));
        }

        return eventDto;
    }

    private ResponsDto validateEvent(EventEntity eventEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(eventEntity.getTitle())) {
            res.addErrors("title", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(eventEntity.getContent())) {
            res.addErrors("content", new String[]{requireMsg});
        }
        if (eventEntity.getStartDate() == null) {
            res.addErrors("start_date", new String[]{requireMsg});
        }
        if (eventEntity.getEndDate() == null) {
            res.addErrors("end_date", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateEvent(HttpServletRequest request, long eventId, long userPk) {


        Map<String, String[]> param = request.getParameterMap();

        boolean isNew = eventId >= 0 ? false : true;
        String[] tels = new String[3];

        Map<String, Object> newMap = new HashMap<>();
        Map<String, Long> fileMap = new HashMap<>();
        List<String> delFiles = new ArrayList<>();
        List<String> changeFiles = new ArrayList<>();
        boolean shopStatus = false;

        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);

            for (String v : vals) {
                System.out.println("key : " + k + ", val : " + v);
                if ("tel_1".equals(k)) {
                    tels[0] = v;
                }
                if ("tel_2".equals(k)) {
                    tels[1] = v;
                }
                if ("tel_3".equals(k)) {
                    tels[2] = v;
                }
                if ("shop_status".equals(k)) {
                    shopStatus = "1".equals(v) ? true : false;
                }

                if (!k.contains("[")) {

                    if ("shop_id".equals(k)) {
                        newMap.put(k, Integer.parseInt(v));
                    } else {
                        newMap.put(k, v);
                    }

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
        EventEntity eventEntity = gson.fromJson(jsonElement, EventEntity.class);

        eventEntity.setShopStatus(shopStatus);

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateEvent(eventEntity);

        String requedErrorMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(tels[0])) {
            validationRes.addErrors("tel_1", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone1(tels[0])) {
            validationRes.addErrors("tel_1"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_1"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[1])) {
            validationRes.addErrors("tel_2", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone2(tels[1])) {
            validationRes.addErrors("tel_2"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_2"
                            , null, LocaleContextHolder.getLocale())});
        }
        if (!StringUtils.hasLength(tels[2])) {
            validationRes.addErrors("tel_3", new String[]{requedErrorMsg});
        } else if (!ElbigsUtil.isValidPhone3(tels[2])) {
            validationRes.addErrors("tel_3"
                    , new String[]{messageSource.getMessage("error.msg.invalid-tel_3"
                            , null, LocaleContextHolder.getLocale())});
        }

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        if (tels[0] != null && tels[1] != null && tels[2] != null) {
            eventEntity.setTel(tels[0] + "-" + tels[1] + "-" + tels[2]);
        }
        eventEntity.setId(eventId);
        eventEntity.setUserId(userPk);

        /* eventEntity save */
        if (!isNew) {
            eventMapper.updateEvent(eventEntity);
        } else {
            eventMapper.insertEvent(eventEntity);
            eventId = eventEntity.getId();
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
            commonMapper.deleteFiles(connectableTypeEvent, eventId, fileMap.get(f));
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
                        .connectableId(eventId)
                        .connectableType(connectableTypeEvent)
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

    public void deleteEvent(long id) {
        eventMapper.deleteEvent(id);
    }
}

