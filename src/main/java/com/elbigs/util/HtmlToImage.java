package com.elbigs.util;

import com.pdfcrowd.Pdfcrowd;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HtmlToImage {

    @Value("${pdfcrowd.auth.user}")
    public String PDFCROWD_USER_NAME;

    @Value("${pdfcrowd.auth.api-key}")
    public String PDFCROWD_API_KEY;

    public boolean convertToImage(String url, String imagePath, String format) {

        boolean isSuccess = true;

        try {
            // create the API client instance
            Pdfcrowd.HtmlToImageClient client =
                    new Pdfcrowd.HtmlToImageClient(PDFCROWD_USER_NAME, PDFCROWD_API_KEY);

            client.setDisableJavascript(true);
            client.setScreenshotWidth(1920);
            client.setScreenshotHeight(1080);
            client.setScaleFactor(50);
            client.setOutputFormat(format);
            client.convertUrlToFile(url, imagePath);
//            client.convertFileToFile(url, imagePath);

        } catch (Exception why) {
            // report the error
            isSuccess = false;
            System.err.println("IO Error: " + why);

        }
        return isSuccess;
    }

}
