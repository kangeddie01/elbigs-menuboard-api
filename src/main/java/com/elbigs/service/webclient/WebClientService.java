package com.elbigs.service.webclient;

import com.elbigs.entity.UserEntity;
import com.elbigs.entity.WeatherEntity;
import com.elbigs.mapper.CommonMapper;
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


    @Value("${external.weather.url}")
    String WEATHER_URL;

    @Value("${weather.service-key}")
    String SERVICE_KEY;

    @Autowired
    private CommonMapper commonMapper;

    public boolean syncWeather(UserEntity user) {


        try {

            String baseDate = DateUtil.getCurrDateStr(DateUtil.FORMAT_02);
            String baseTime = DateUtil.getAddHour(-1, DateUtil.FORMAT_03);
            int nx = (int) user.getAreaX();
            int ny = (int) user.getAreaY();

            String url = WEATHER_URL +
                    "?pageNo=1" +
                    "&numOfRows=100" +
                    "&ServiceKey=" + SERVICE_KEY +
                    "&dataType=JSON" +
                    "&base_date=" + baseDate +
                    "&base_time=" + baseTime + "00" +
                    "&nx=" + nx +
                    "&ny=" + ny;

            System.out.println(url);
//        String body = template.getForObject(url, String.class);
            ResponseEntity<String> body = template.getForEntity(url, String.class);
            JsonElement json = JsonParser.parseString(body.getBody());
            System.out.println("json : " + json);
            JsonElement items = json.getAsJsonObject().get("response").getAsJsonObject().get("body").getAsJsonObject().get("items");
            JsonArray arr = items.getAsJsonObject().get("item").getAsJsonArray();

            String ptyLastTime = null;
            String skyLastTime = null;
            String t1hLastTime = null;
            String skyVal = null;
            String ptyVal = null;
            String t1hVal = null;
            for (JsonElement j : arr) {
//            System.out.println(j.toString());
                JsonObject itemObj = j.getAsJsonObject();
                String category = itemObj.get("category").getAsString();
                String fcstTime = itemObj.get("fcstTime").getAsString();
                String fcstValue = itemObj.get("fcstValue").getAsString();

                if (category.equals("PTY") && (ptyLastTime == null || fcstTime.compareTo(ptyLastTime) > 0)) {
                    ptyLastTime = fcstTime;
                    ptyVal = fcstValue;
                }
                if (category.equals("SKY") && (skyLastTime == null || fcstTime.compareTo(skyLastTime) > 0)) {
                    skyLastTime = fcstTime;
                    skyVal = fcstValue;
                }
                if (category.equals("T1H") && (t1hLastTime == null || fcstTime.compareTo(t1hLastTime) > 0)) {
                    t1hLastTime = fcstTime;
                    t1hVal = fcstValue;
                }
//            System.out.println("category : " + itemObj.get("category") + ", fcstTime : "
//                    + itemObj.get("fcstTime") + ", fcstValue : " + itemObj.get("fcstValue"));
            }

            String[] pty1 = {"2", "3", "6", "7"};
            String[] pty2 = {"1", "4", "5"};
            String skyResult = null;
            if (Arrays.asList(pty1).contains(ptyVal)) {
                skyResult = "E";
            } else if (Arrays.asList(pty2).contains(ptyVal)) {
                skyResult = "D";
            } else if (skyVal.equals("4")) {
                skyResult = "C";
            } else if (skyVal.equals("4")) {
                skyResult = "B";
            } else {
                skyResult = "A";
            }

            logger.info("[" + user.getUserId() + "] sky : " + skyResult + ", temperature : " + t1hVal);

            if (!StringUtils.hasLength(skyResult)) {
                return false;
            }

            WeatherEntity entity = WeatherEntity.builder()
                    .userId(user.getId())
                    .date(baseDate)
                    .time(skyLastTime)
                    .sky(skyResult)
                    .imgFile(skyResult + ".png")
                    .temperature(t1hVal)
                    .build();
            commonMapper.insertWeather(entity);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void test2() {
        RestTemplate template = new RestTemplate();
        String body = template.getForObject("http://www.naver.com", String.class);
    }
}
