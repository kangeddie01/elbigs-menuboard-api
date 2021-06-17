package com.elbigs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ShopReqDto extends PagingParam {

    private long id;

    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("search_str")
    private String searchStr;
    private long categoryId;
    private long userPk;
    private long busStopPk;

    private String foSearch;
    private String lang;
}
