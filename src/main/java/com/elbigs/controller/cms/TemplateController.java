package com.elbigs.controller.cms;

import com.elbigs.dto.ResponseDto;
import com.elbigs.entity.ShopEntity;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cms")
public class TemplateController {


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



}
