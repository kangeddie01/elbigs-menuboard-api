package com.elbigs.service.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebClientService {
    private RestTemplate template = new RestTemplate();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
