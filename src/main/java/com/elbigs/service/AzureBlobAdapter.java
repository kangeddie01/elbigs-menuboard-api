package com.elbigs.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class AzureBlobAdapter {

    @Autowired
    BlobClientBuilder client;


    public String upload(MultipartFile file, String uploadedPath) {
        if (file != null && file.getSize() > 0) {
            try {
                String ext = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 3);
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
