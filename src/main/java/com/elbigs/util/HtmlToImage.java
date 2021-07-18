package com.elbigs.util;

import com.pdfcrowd.Pdfcrowd;

public class HtmlToImage {


    public static boolean convertToImage(String url, String imagePath, String format) {

        boolean isSuccess = true;

        try {
            // create the API client instance
            Pdfcrowd.HtmlToImageClient client =
                    new Pdfcrowd.HtmlToImageClient("kangeddie01", "b071095f978e67ff37fbb3afbd31f51a");

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
