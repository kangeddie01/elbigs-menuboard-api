package com.elbigs.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class ResponsDto extends HashMap {

    public ResponsDto() {
        this.put("success", true);
    }

    public void setSuccess(boolean success) {
        this.put("success", success);
    }

    public boolean isSuccess() {
        return (Boolean) this.get("success");
    }

    public void addErrors(String errorKey, String[] errorMsgs) {

        if (this.get("errors") == null) {
            this.put("errors", new HashMap<String, String[]>());
        }
        Map<String, String[]> errorCtnts = (Map<String, String[]>) this.get("errors");
        errorCtnts.put(errorKey, errorMsgs);

        this.put("success", false);
    }

    public void putMap(Map<String, Object> map) {
        this.putAll(map);
    }
}
