package com.elbigs.controller.front;

import com.elbigs.dto.ResponseDto;
import com.elbigs.dto.ResponseDto2;
import com.elbigs.entity.ShopDisplayEntity;
import com.elbigs.service.CommonService;
import com.elbigs.service.ShopService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cms")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AppApiController {

    @Autowired
    private ShopService shopService;

    /**
     * 서버 체크하여 갱신 데이터가 있으면 download url 을 리턴한다.
     * @param settopId
     * @return
     */
    @ApiOperation(value="서버 체크" , notes = "<b>서버 상태를 체크한다.</b> \n\n\n " +
            "[resType]\n\n 1.NOT_REGISTER - 셋탑 정보 미등록\n\n 2.NO_DISPLAY - 전시설정정보 없음\n\n 3.MODIFIED - 전시정보 갱신\n\n 4.NO_CHANGE - 변경없음\n\n 5.NOT_AVAILABLE - 패널정보 비활성\n\n" +
            "MODIFIED 일 경우 downloadUrl 을 리턴한다.")
    @GetMapping("/{settopId}/check")
    public ResponseDto checkServer(@PathVariable("settopId") String settopId) {
        ResponseDto res = new ResponseDto();

        Map<String, String> resMap = shopService.checkServer(settopId);
        res.setSuccess(true);
        res.put("resType", resMap.get("resType"));
        res.put("downloadUrl", resMap.get("downloadUrl"));

        return res;
    }
}
