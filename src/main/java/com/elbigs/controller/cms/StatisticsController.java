package com.elbigs.controller.cms;

import com.elbigs.dto.ShopReqDto;
import com.elbigs.service.StatisticsService;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.StatisticsDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/cms")
public class StatisticsController {


    @Autowired
    private StatisticsService statisticsService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/report/dashboard/network")
    public ResponsDto dashboardNetwork(@RequestParam(name = "selected_user_id", required = false, defaultValue = "0") long userPk) throws Exception {
        ResponsDto res = new ResponsDto();
        List<Map<String, Object>> dataList = new ArrayList<>();


        Map<String, Long> networkCnt = statisticsService.selectNetworkCount(userPk);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "키오스크 대수");
        dataMap.put("count", networkCnt.get("total_cnt") == null ? 0 : networkCnt.get("total_cnt"));
        dataList.add(dataMap);

        Map<String, Object> dataMap2 = new HashMap<>();
        dataMap2.put("name", "정상");
        dataMap2.put("count", networkCnt.get("active_cnt") == null ? 0 : networkCnt.get("active_cnt"));
        dataList.add(dataMap2);

        Map<String, Object> dataMap3 = new HashMap<>();
        dataMap3.put("name", "비정상");
        dataMap3.put("count", networkCnt.get("deactive_cnt") == null ? 0 : networkCnt.get("deactive_cnt"));
        dataList.add(dataMap3);

        res.put("summaryInfo", dataList);
        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/report/dashboard/visit")
    public ResponsDto dashboardVisit(@RequestParam(name = "selected_user_id", required = false, defaultValue = "0") long userPk) throws Exception {
        ResponsDto res = new ResponsDto();

        List<Map<String, Object>> visitCount = statisticsService.selectVisitCount(userPk);

        res.put("summaryInfo", visitCount);
        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/report/dashboard/chart")
    public ResponsDto chart(@RequestParam(name = "selected_user_id", required = false, defaultValue = "0") long userPk) throws Exception {

        List<StatisticsDto> lists = statisticsService.selectChartType1();
        Map<Integer, Double> dataArr = new HashMap<>();


        for (StatisticsDto s : lists) {
            dataArr.put(s.getDateIndex(), Math.round(Double.parseDouble(s.getAvg()) * 100) / 100.0);
        }

        String[] lable2 = new String[7];
        Double[] dataList = new Double[7];
        Long[] dataList2 = new Long[7];

        for (int i = 0; i < 7; i++) {
            lable2[i] = DateUtil.getAddDate((i - 7), "MM월 dd일(E)", Locale.KOREAN);
            Double d = dataArr.get(Integer.parseInt(DateUtil.getAddDate(i - 7, "u", null)));
            dataList[i] = d == null ? 0d : d;
        }

        List<Map<String, Object>> datasets = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("label", "요일별 평균 페이지뷰");
        data1.put("data", dataList);
        data1.put("backgroundColor", "rgba(245,121,164,0.4)");
        datasets.add(data1);

        List<StatisticsDto> list2 = statisticsService.selectChartType2(userPk);

        int i = 0;
        for (StatisticsDto s : list2) {
            dataList2[i++] = Long.parseLong(s.getCount());
        }

        Map<String, Object> data2 = new HashMap<>();
        data2.put("label", "일자별 페이지뷰");
        data2.put("data", dataList2);
        data2.put("backgroundColor", "rgba(58,191,239,0.4)");
        datasets.add(data2);

        ResponsDto res = new ResponsDto();
        res.put("datasets", datasets);
        res.put("labels", lable2);

        return res;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/report/statistics")
    public ResponsDto statList(@RequestParam(name = "selected_user_id", required = false, defaultValue = "0") long userPk
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) {

        ShopReqDto req = new ShopReqDto();
        req.setUserPk(userPk);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> bodyList = new ArrayList<>();


        List<StatisticsDto> dataList = statisticsService.selectDailyStatList(endDate, userPk, DateUtil.getDayDiff(startDate, endDate));

        Map<String, Object> agentMap = new LinkedHashMap<>();
        String currAgentName = null;
        double currAvg = 0;
        long currSum = 0;

        for (StatisticsDto stat : dataList) {
            if (!stat.getAgentName().equals(currAgentName)) {// agent 시작 시점

                if (currAgentName != null) { // agent 변경 시점 ( 다음 agent )
                    agentMap.put("avg", currAvg);
                    agentMap.put("total", currSum);
                    bodyList.add(agentMap);

                    agentMap = new LinkedHashMap<>();
                }

                agentMap.put("name", stat.getUserName());
                agentMap.put("agent_name", stat.getAgentName());
                agentMap.put("mac_address", stat.getMacAddress());

                currAgentName = stat.getAgentName();
                currAvg = Double.parseDouble(ElbigsUtil.ifNull(stat.getAvg(), "0"));
                currSum = Long.parseLong(ElbigsUtil.ifNull(stat.getTotal(), "0"));

            }

            agentMap.put(stat.getEventDate(), stat.getCount() == null ? "-" : Long.parseLong(stat.getCount()));
        }

        agentMap.put("avg", currAvg);
        agentMap.put("total", currSum);
        bodyList.add(agentMap);
        resultMap.put("body", bodyList);

        /* 헤더 정보 */
        List<String> headList = new ArrayList<>();
        headList.add("agent_name");

        long diff = DateUtil.getDayDiff(endDate, startDate);
        for (int i = 0; i >= -1 * diff; i--) {
            headList.add(DateUtil.getAddDateFromDate(i, endDate));
        }
        headList.add("avg");
        headList.add("total");

        resultMap.put("head", headList);

        ResponsDto res = new ResponsDto();
        res.put("data", resultMap);

        return res;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/report/statistics/all")
    public ResponsDto statListAll(@RequestParam(name = "search_str", required = false) String searchStr
            , @RequestParam(name = "start_date", required = false) String startDate
            , @RequestParam(name = "end_date", required = false) String endDate) {

        ShopReqDto req = new ShopReqDto();
        req.setSearchStr(searchStr);
        req.setStartDate(startDate);
        req.setEndDate(endDate);

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> bodyList = new ArrayList<>();

        List<StatisticsDto> dataList = statisticsService.selectDailyStatAllList(endDate, searchStr, DateUtil.getDayDiff(startDate, endDate));

        Map<String, Object> agentMap = new LinkedHashMap<>();
        String currUserName = null;
        String currAvg = null;
        String currTotal = null;
        String maxAgent = null;
        String minAgent = null;

        for (StatisticsDto stat : dataList) {
            if (!stat.getUserName().equals(currUserName)) {// agent 시작 시점

                if (currUserName != null) { // 상인회 변경 시점 ( 다음 상인회 )

                    agentMap.put("max", ElbigsUtil.ifNull(maxAgent, "-"));
                    agentMap.put("min", ElbigsUtil.ifNull(minAgent, "-"));
                    agentMap.put("avg", currAvg == null ? "-" : Double.parseDouble(currAvg));
                    agentMap.put("total", currTotal == null ? "-" : Long.parseLong(currTotal));
                    bodyList.add(agentMap);

                    agentMap = new LinkedHashMap<>();
                }

                agentMap.put("name", stat.getUserName());

                currUserName = stat.getUserName();
                currAvg = stat.getAvg();
                currTotal = stat.getTotal();
                maxAgent = stat.getMaxAgent();
                minAgent = stat.getMinAgent();

            }

            agentMap.put(stat.getEventDate(), stat.getCount() == null ? "-" : Long.parseLong(stat.getCount()));
        }
        agentMap.put("max", ElbigsUtil.ifNull(maxAgent, "-"));
        agentMap.put("min", ElbigsUtil.ifNull(minAgent, "-"));
        agentMap.put("avg", currAvg == null ? "-" : Double.parseDouble(currAvg));
        agentMap.put("total", currTotal == null ? "-" : Long.parseLong(currTotal));
        bodyList.add(agentMap);
        resultMap.put("body", bodyList);

        /* 헤더 정보 */
        List<String> headList = new ArrayList<>();
        headList.add("name");

        long diff = DateUtil.getDayDiff(endDate, startDate);
        for (int i = 0; i >= -1 * diff; i--) {
            headList.add(DateUtil.getAddDateFromDate(i, endDate));
        }
        headList.add("max");
        headList.add("min");
        headList.add("avg");
        headList.add("total");

        resultMap.put("head", headList);

        ResponsDto res = new ResponsDto();
        res.put("data", resultMap);

        return res;
    }
}
