package com.elbigs.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTMLTagFilterRequestWrapper extends HttpServletRequestWrapper {

    public HTMLTagFilterRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> param = super.getParameterMap();
        Map<String, String[]> result = new HashMap<>();
        for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
            String k = (String) it.next();
            String[] vals = param.get(k);
            for (String v : vals) {
//                System.out.println("key : " + k + ", val : " + v);
            }
            try {
//                result.put(URLEncoder.encode(k, "UTF-8"), vals);
            } catch (Exception e) {

            }
        }
        System.out.println("parameter size : " + result.size());
        return result;
    }
}
