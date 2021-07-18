package com.elbigs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 디렉토리 카피
     *
     * @param sourceF
     * @param targetF
     */
    public static void copyDir(File sourceF, File targetF) {


        File[] target_file = sourceF.listFiles();
        if (target_file == null) {
            return;
        }
        for (File file : target_file) {

            System.out.println("target ::" + targetF.getAbsolutePath() + File.separator + file.getName());
            File temp = new File(targetF.getAbsolutePath() + File.separator + file.getName());
            if (file.isDirectory()) {
                temp.mkdir();
                copyDir(file, temp);
            } else {
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    fis = new FileInputStream(file);
                    fos = new FileOutputStream(temp);
                    byte[] b = new byte[4096];
                    int cnt = 0;
                    while ((cnt = fis.read(b)) != -1) {
                        fos.write(b, 0, cnt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                        fos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
