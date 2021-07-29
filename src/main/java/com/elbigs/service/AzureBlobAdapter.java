package com.elbigs.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class AzureBlobAdapter {

    @Autowired
    BlobClientBuilder client;

    /**
     * image data url 을 바이너리로 변환하여 Azure에 upload 하기
     *
     * @param imageDataUrl
     * @param uploadedPath
     * @return
     */
    public void upload(String imageDataUrl, String uploadedPath) {

        try {

            byte[] imagedata = DatatypeConverter.parseBase64Binary(imageDataUrl.substring(imageDataUrl.indexOf(",") + 1));
            InputStream inputStream = new ByteArrayInputStream(imagedata);

            client.blobName(uploadedPath).buildClient().upload(inputStream, imagedata.length, true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(InputStream inputStream, String uploadedPath, int length) {

        try {
            client.blobName(uploadedPath).buildClient().upload(inputStream, length, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 로컬 파일을 업로드 한다.
     *
     * @param uploadedPath 업로드할 경로
     * @param localFilePath     업로드할 파일 디스크 경로
     */
    public void uploadLocalFile(String uploadedPath, String localFilePath) {
        client.blobName(uploadedPath).buildClient().uploadFromFile(localFilePath, true);
    }

    /**
     * multipart file upload
     *
     * @param file
     * @param uploadedPath
     * @return
     */
    public String upload(MultipartFile file, String uploadedPath) {
        if (file != null && file.getSize() > 0) {
            try {
                System.out.println("fileName final : " + uploadedPath);
                client.blobName(uploadedPath).buildClient().upload(file.getInputStream(), file.getSize());
                return uploadedPath;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public byte[] getFile(String name) {
        try {

            File temp = new File("/download", "beVZw1kuwg1609997177.jpeg");
            temp.createNewFile();

            BlobProperties properties = client.blobName("/20210107/beVZw1kuwg1609997177.jpeg").buildClient().downloadToFile(temp.getPath());
            byte[] content = Files.readAllBytes(Paths.get(temp.getPath()));
            temp.delete();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteFile(String name) {
        try {
            client.blobName(name).buildClient().delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
