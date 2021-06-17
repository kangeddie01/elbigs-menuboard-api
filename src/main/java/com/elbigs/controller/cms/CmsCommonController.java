package com.elbigs.controller.cms;

import com.elbigs.dto.ResponsDto;
import com.elbigs.service.AzureBlobAdapter;
import com.elbigs.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/v1/cms")
public class CmsCommonController {

    @Autowired
    private CommonService commonService;

    @Autowired
    AzureBlobAdapter azureAdapter;

    @GetMapping("/initial-search")
    public ResponsDto selectInitialSearchList() {

        ResponsDto res = new ResponsDto();
        res.put("initial_search_list", commonService.selectInitialSearchList());

        return res;
    }


    @GetMapping(path = "/files/download")
    public void uploadFile(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "file_path") String fileUrl, @RequestParam(value = "file_name") String fileName) throws IOException {
        URL url = null;
        InputStream in = null;
        OutputStream out = null;

        try {

            String header = request.getHeader("User-Agent");
            if (header.contains("MSIE") || header.contains("Trident")) {
                fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ";");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            }

            response.setHeader("Pragma", "no-cache;");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");

            out = response.getOutputStream();

            String httpsResult = "";

            url = new URL(fileUrl);
            // 만약 프로토콜이 https 라면 https SSL을 무시하는 로직을 수행해주어야 한다.('https 인증서 무시' 라는 키워드로 구글에 검색하면 많이 나옵니다.)

            in = url.openStream();

            while (true) {
                //파일을 읽어온다.
                int data = in.read();
                if (data == -1) {
                    break;
                }
                //파일을 쓴다.
                out.write(data);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }
}
