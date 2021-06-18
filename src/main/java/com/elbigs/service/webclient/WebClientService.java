package com.elbigs.service.webclient;

import com.elbigs.entity.UserEntity;
import com.elbigs.mybatisMapper.CommonMapper;
import com.elbigs.util.DateUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class WebClientService {
    private RestTemplate template = new RestTemplate();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
