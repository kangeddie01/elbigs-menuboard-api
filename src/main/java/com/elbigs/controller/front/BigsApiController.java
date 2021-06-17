package com.elbigs.controller.front;

import com.elbigs.dto.*;
import com.elbigs.entity.*;
import com.elbigs.mapper.*;
import com.elbigs.service.*;
import com.elbigs.util.ElbigsUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kiosk")
public class BigsApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private MenuCategoryMapper menuCategoryMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private TourismService tourismService;

    @Autowired
    private AdverService adverService;

    @Autowired
    private LineNoticeMapper lineNoticeMapper;

    @Autowired
    private UserShopService userShopService;

    @Autowired
    private UserShopMapper userShopMapper;

    @Autowired
    private BusStopMapper busStopMapper;

    @Autowired
    private FacilityService facilityService;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @GetMapping("/users")
    public List<UserEntity> selectUserList(UserParamDto userParamDto) {
        return userService.selectUserListAll(userParamDto);
    }

    @GetMapping("/kiosks/{macAddress}/shops/initial/{initialSearchId}")
    public ResponsDto check(@PathVariable("macAddress") String macAddress, @PathVariable("initialSearchId") long initialSearchId) {
        ResponsDto res = new ResponsDto();
        Map<String, Object> param = new HashMap<>();

        KioskEntity kiosk = userMapper.selectKioskByMac(macAddress);

        param.put("initialSearchId", initialSearchId);
        param.put("userPk", kiosk.getUserId());

        List<ShopEntity> list = userShopMapper.selectShopSearch(param);
        res.put("shopList", list);

        return res;
    }

    @GetMapping("/kiosks/{macAddress}/check")
    public ResponsDto check(@PathVariable("macAddress") String macAddress) {
        return new ResponsDto();
    }

    @GetMapping("/kiosks/{macAddress}/shops/{shopId}")
    public ResponsDto shop(@PathVariable("macAddress") String macAddress
            , @PathVariable("shopId") long shopId) {
        ResponsDto res = new ResponsDto();

        res.put("shopInfo", userShopService.selectShopDetailFo(shopId));
        return res;
    }

    @GetMapping("/kiosks/{macAddress}/facilities/{facilityId}")
    public ResponsDto facilities(@PathVariable("macAddress") String macAddress
            , @PathVariable("facilityId") long facilityId) {
        ResponsDto res = new ResponsDto();

        res.put("facilityInfo", facilityService.selectFacilityDetailFo(facilityId));
        return res;
    }

    @PostMapping("/settings")
    public ResponsDto settings(@RequestBody KioskFoDto kioskEntity) {
        ResponsDto res = new ResponsDto();

        String key = kioskEntity.getKey();
        String mac = kioskEntity.getMac();

        if (key == null || key.length() != 6) {
            res.addErrors("key", new String[]{"6자 정확한 key 값을 입력 바랍니다."});
        }

        if (mac == null || mac.length() != 12) {
            res.addErrors("mac", new String[]{"12자 정확한 맥 주소 값을 입력 바랍니다."});
        }
        if (!res.isSuccess()) {
            return res;
        }

        KioskEntity kiosk = userMapper.selectKioskByKey(key);

        if (kiosk == null) {
            res.addErrors("key", new String[]{"정상적인 key 값이 아닙니다. 확인 후 다시 이용 바랍니다."});
        } else {
            res = selectUserByMac(kiosk.getMacAddress());
        }

        return res;
    }

    @GetMapping("/kiosks/{macAddress}/settings")
    public ResponsDto selectUserList(@PathVariable("macAddress") String macAddress) {
        ResponsDto res = selectUserByMac(macAddress);
        return res;
    }

    public ResponsDto selectUserByMac(String macAddress) {

        ResponsDto res = new ResponsDto();

        KioskEntity kiosk = userMapper.selectKioskByMac(macAddress);

        UserEntity user = userMapper.selectUser(kiosk.getUserId());
        UserSettingDto settingDto = userService.selectUserSetting(user.getId());

        /* userInfo */
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("tel", settingDto.getTel());
        userInfo.put("kakaoZoomLevel", user.getKakaoZoomLevel());
        userInfo.put("time", settingDto.getOpenTime() + " ~ " + settingDto.getCloseTime());
        res.put("userInfo", userInfo);

        /* zoomLevel */
        Map<String, Object> zoomLevel = new HashMap<>();
        zoomLevel.put("defaultZoom", user.getDefaultZoom());
        zoomLevel.put("minZoom", user.getMinZoom());
        zoomLevel.put("maxZoom", user.getMaxZoom());
        res.put("zoomLevel", zoomLevel);

        /* poiLevel */
        Integer[] poiLevel = new Integer[]{user.getPoiLevelH(), user.getPoiLevelM(), user.getPoiLevelL()};
        res.put("poiLevel", poiLevel);

        /* menuCategories */
        List<MenuCategoryDto> menuCategoryDtoList = menuCategoryMapper.selectUserMenuCategoryForFo(user.getId());
        Map<String, Object> categoryMap = new HashMap<>();
        List<Map<String, Object>> menuCategories = new ArrayList<>();
        Map<String, Object> categoryNameMap = new HashMap<>();
        for (MenuCategoryDto categoryDto : menuCategoryDtoList) {
            categoryMap.put("id", categoryDto.getId());
            categoryMap.put("image", categoryDto.getImage());
            categoryNameMap.put("ko", categoryDto.getName());
            categoryNameMap.put("en", ElbigsUtil.ifNull(categoryDto.getEnName(), categoryDto.getName()));
            categoryNameMap.put("jp", ElbigsUtil.ifNull(categoryDto.getJpName(), categoryDto.getName()));
            categoryNameMap.put("cn", ElbigsUtil.ifNull(categoryDto.getCnName(), categoryDto.getName()));
            categoryMap.put("name", categoryNameMap);
            menuCategories.add(categoryMap);
            categoryNameMap = new HashMap<>();
            categoryMap = new HashMap<>();
        }
        res.put("menuCategories", menuCategories);

        /* initialSearchList */
        List<InitialSearchEntity> initialSearchEntities = commonMapper.selectInitialSearchList();
        List<Map<String, Object>> initialList = new ArrayList<>();
        for (InitialSearchEntity initial : initialSearchEntities) {
            Map<String, Object> initialMap = new HashMap<>();
            initialMap.put("id", initial.getId());
            initialMap.put("initial", initial.getExpressLetter());
            initialList.add(initialMap);
        }
        res.put("initialSearchList", initialList);

        /* mapInfo */
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("mapClientId", user.getMapClientId());
        mapInfo.put("mapClientSecret", user.getMapClientSecret());
        res.put("mapInfo", mapInfo);

        /* weather */
        List<WeatherEntity> weatherList = commonMapper.selectWeatherList(user.getId());
        if (weatherList != null && weatherList.size() > 0) {
            Map<String, Object> weatherMap = new HashMap<>();
            for (WeatherEntity weather : weatherList) {
                weatherMap.put("image", weather.getImgFile());
                weatherMap.put("temperature", weather.getTemperature());
            }
            res.put("weather", weatherMap);
        } else {
            res.put("weather", new ArrayList());
        }
        /* myPosition */
        Map<String, Object> myPosition = new HashMap<>();
        Map<String, Object> position = new HashMap<>();
        position.put("x", kiosk.getX());
        position.put("y", kiosk.getY());
        myPosition.put("position", position);
        myPosition.put("floorId", kiosk.getFloorId());
        res.put("myPosition", myPosition);

        /* mainSettings */
        Map<String, Object> mainSettings = new HashMap<>();


        mainSettings.put("headerLogo", ElbigsUtil.ifEmpty(settingDto.getLogoImg(), azureStorageUrl + "/default_logo.png"));
        mainSettings.put("storeLogo", ElbigsUtil.ifEmpty(settingDto.getStoreImg(), azureStorageUrl + "/store.png"));
        mainSettings.put("trafficLogo", ElbigsUtil.ifEmpty(settingDto.getTrafficImg(), azureStorageUrl + "/transportation.png"));
        mainSettings.put("facilitiesLogo", ElbigsUtil.ifEmpty(settingDto.getFacilitiesImg(), azureStorageUrl + "/facilities.png"));
        mainSettings.put("attractionsLogo", ElbigsUtil.ifEmpty(settingDto.getAttractionsImg(), azureStorageUrl + "/attractions.png"));

        Map<String, Object> slogans = new HashMap<>();

        Map<String, Object> slogan1 = new HashMap<>();
        slogan1.put("isBold", settingDto.isSlogan1Bold());
        Map<String, Object> message = new HashMap<>();
        message.put("ko", settingDto.getSlogan1());
        message.put("en", settingDto.getEnSlogan1());
        message.put("cn", settingDto.getCnSlogan1());
        message.put("jp", settingDto.getJpSlogan1());
        slogan1.put("message", message);

        Map<String, Object> slogan2 = new HashMap<>();
        slogan2.put("isBold", settingDto.isSlogan2Bold());
        message = new HashMap<>();
        message.put("ko", settingDto.getSlogan2());
        message.put("en", settingDto.getEnSlogan2());
        message.put("cn", settingDto.getCnSlogan2());
        message.put("jp", settingDto.getJpSlogan2());
        slogan2.put("message", message);

        Map<String, Object> slogan3 = new HashMap<>();
        slogan3.put("isBold", settingDto.isSlogan3Bold());
        message = new HashMap<>();
        message.put("ko", settingDto.getSlogan3());
        message.put("en", settingDto.getEnSlogan3());
        message.put("cn", settingDto.getCnSlogan3());
        message.put("jp", settingDto.getJpSlogan3());
        slogan3.put("message", message);

        slogans.put("slogan1", slogan1);
        slogans.put("slogan2", slogan2);
        slogans.put("slogan3", slogan3);
        mainSettings.put("slogans", slogans);
        res.put("mainSettings", mainSettings);


        /* eventList */
        ShopReqDto searchEvent = new ShopReqDto();
        searchEvent.setUserPk(user.getId());
        searchEvent.setFoSearch("Y");
        List<EventDto> eventDtoList = eventService.selectEventList2(searchEvent);
        List<Map<String, Object>> eventListMap = new ArrayList<>();
        Map<String, Object> eventList = new HashMap<>();

        String[] langList = new String[]{"ko", "jp", "cn", "en"};

        if (eventDtoList != null && eventDtoList.size() > 0) {

            for (String lang : langList) {

                for (EventDto event : eventDtoList) {

                    Map<String, Object> eventMap = new HashMap<>();
                    List<FileEntity> files = event.getFiles();
                    if (files != null && files.size() > 0) {
                        List<String> banners = new ArrayList<>();
                        for (FileEntity f : files) {
                            banners.add(f.getFullPath());
                        }
                        eventMap.put("banners", banners);
                    } else {
                        eventMap.put("banners", null);
                    }

                    if ("ko".equals(lang)) {
                        eventMap.put("title", event.getTitle());
                        eventMap.put("description", event.getContent());
                    } else if ("en".equals(lang)) {
                        eventMap.put("title", event.getEnTitle());
                        eventMap.put("description", event.getEnContent());
                    } else if ("jp".equals(lang)) {
                        eventMap.put("title", event.getJpTitle());
                        eventMap.put("description", event.getJpContent());
                    } else if ("cn".equals(lang)) {
                        eventMap.put("title", event.getCnTitle());
                        eventMap.put("description", event.getCnContent());
                    }

                    eventMap.put("eventTime", event.getStartDate() + " ~ " + event.getEndDate());
                    eventMap.put("id", event.getId());
                    eventMap.put("place", "");
                    eventMap.put("shopId", event.getShopId());
                    eventMap.put("shopStatus", event.isShopStatus());
                    eventListMap.add(eventMap);
                }

                eventList.put(lang, eventListMap);
                eventListMap = new ArrayList<>();
            }

            res.put("eventList", eventList);

        } else {
            res.put("eventList", new ArrayList());
        }


        /* attractionList */
        ShopReqDto searchTour = new ShopReqDto();
        searchTour.setUserPk(user.getId());
        Map<String, Object> attractionList = tourismService.selectTourismListFo(searchTour);
        if (attractionList == null) {
            res.put("attractionList", new ArrayList());
        } else {
            res.put("attractionList", attractionList);
        }


        /* adList */
        res.put("adList", adverService.selectAdListByKiosk(macAddress, user.getId()));

        /* naviSpeed */
        res.put("naviSpeed", user.getNaviSpeed());

        /* kioskAddr */
        res.put("kioskAddr", kiosk.getAddr());

        /* lineNoticeList */
        List<String> lineNotices = lineNoticeMapper.selectLineNoticeListForFo(user.getId());

        Map<String, List> lineNoticeList = new HashMap<>();
        if (lineNoticeList != null && lineNoticeList.size() > 0) {
            lineNoticeList.put("ko", lineNotices);
            lineNoticeList.put("cn", lineNotices);
            lineNoticeList.put("jp", lineNotices);
            lineNoticeList.put("en", lineNotices);
            res.put("lineNoticeList", lineNoticeList);
        } else {
            res.put("lineNoticeList", new ArrayList());
        }

        /* popupNotice */
        HashMap<String, String> popupNotice = lineNoticeMapper.selectPopupNoticeListForFo(user.getId());
        if (popupNotice != null && popupNotice.size() > 0) {
            res.put("popupNotice", popupNotice);
        } else {
            res.put("popupNotice", new ArrayList());
        }

        /* floorList */
        List<FloorEntity> floorList = userMapper.selectFloorList(user.getId());
        JsonArray jArray = new JsonArray();
        for (FloorEntity f : floorList) {
            JsonObject j = new JsonObject();
            j.addProperty("id", f.getFloorId());
            j.addProperty("name", f.getName());
            j.addProperty("order", f.getOrder());
            jArray.add(j);
        }
        Gson gson = new Gson();
        res.put("floorList", gson.fromJson(jArray, List.class));

        /* busStopInfo */
        res.put("busStopInfo", busStopMapper.selectBusInfos(user.getId()));

        return res;
    }
}
