package com.elbigs.controller.cms;

import com.elbigs.dto.ResponseDto;
import com.elbigs.entity.HtmlTemplateEntity;
import com.elbigs.entity.ShopEntity;
import com.elbigs.service.DisplayService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import jdk.tools.jlink.internal.plugins.ExcludePlugin;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/cms")
public class TemplateController {


    @Autowired
    private DisplayService displayService;

    /**
     * 템플릿 등록
     *
     * @param entity
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping
    public ResponseDto createTemplate(@RequestBody ShopEntity entity) {
        return null;
    }


    @PostMapping("/upload-template")
    public ResponseDto uploadTemplate(@RequestPart List<MultipartFile> file,
                                      HtmlTemplateEntity entity) {
        ResponseDto res = new ResponseDto();

        if (file != null && file.size() > 0) {

            try {
                displayService.uploadTemplate(file.get(0), entity);
                res.setSuccess(true);
            } catch (IOException e) {
                res.setSuccess(false);
                res.addErrors("501", e.getMessage());
            } catch (ZipException e) {
                res.setSuccess(false);
                res.addErrors("502", e.getMessage());
            } catch (Exception e){
                res.setSuccess(false);
                res.addErrors("503", e.getMessage());
            }

        } else {
            res.addErrors("503", "저장할 파일이 없습니다.");
            res.setSuccess(false);
        }
        return res;
    }

}
