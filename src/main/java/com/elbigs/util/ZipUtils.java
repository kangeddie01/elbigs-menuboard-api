package com.elbigs.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ZipUtils {

    public static final String currPath = System.getProperty("user.dir");

    public static void main(String[] args) {

        String destination = "C:\\project\\elbigs-menuboard-api\\src\\main\\webapp\\displays\\sample1";
        String source = destination + File.separator + "sample1.zip";

        System.out.println(destination);
        System.out.println(source);

//        String destination = currPath + File.separator + "hello";
        try {
            ZipFile zipfile = new ZipFile(source);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipfile.addFolder(destination, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


    public static boolean zipFolder(String sourceDir, String destZipPath) {

        try {
            ZipFile zipfile = new ZipFile(destZipPath);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipfile.addFolder(sourceDir, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
