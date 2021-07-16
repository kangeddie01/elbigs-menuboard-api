package com.elbigs.controller;

import com.pdfcrowd.Pdfcrowd;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;

public class TestController {


    public String sss(String urlPath) {

        String pageContents = "";
        StringBuilder contents = new StringBuilder();

        try {

            URL url = new URL(urlPath);
            URLConnection con = (URLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(con.getInputStream(), "utf-8");

            BufferedReader buff = new BufferedReader(reader);

            while ((pageContents = buff.readLine()) != null) {
                //System.out.println(pageContents);
                contents.append(pageContents);
                contents.append("\r\n");
            }

            buff.close();

            System.out.println(contents.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return contents.toString();
    }

    public static void main(String[] args) throws IOException, Pdfcrowd.Error {
        try {
            // create the API client instance
            Pdfcrowd.HtmlToImageClient client =
                    new Pdfcrowd.HtmlToImageClient("kangeddie01", "b071095f978e67ff37fbb3afbd31f51a");

            client.setOutputFormat("jpg");

            // run the conversion and write the result to a file
            client.convertFileToFile("C:/project/elbigs/elbigs-menuboard-api/src/main/webapp/displays/display_68/display_68.html", "c:\\image\\template3.jpg");


        } catch (Exception why) {
            // report the error
            System.err.println("IO Error: " + why);
            why.printStackTrace();

            throw why;
        }
    }
}
