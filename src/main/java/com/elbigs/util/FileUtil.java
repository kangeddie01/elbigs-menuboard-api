package com.elbigs.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipOutputStream;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;

public class FileUtil {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 디렉토리 카피
     *
     * @param sourceF
     * @param targetF
     */
    public static void copyDir(File sourceF, File targetF) throws IOException {

        if (!targetF.exists()) {
            if (targetF.mkdirs()) {
            }
        }

        File[] target_file = sourceF.listFiles();
        if (target_file == null) {
            return;
        }
        for (File file : target_file) {

//            System.out.println("target ::" + targetF.getAbsolutePath() + File.separator + file.getName());
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
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
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


    /**
     * bytes 를 savePath에 저장
     * @param bytes
     * @param fileName
     * @param savePath
     * @return
     */
    public static boolean writeFile(byte[] bytes, String fileName, String savePath) {

        File dir = new File(savePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(savePath + File.separator + fileName);
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
