package com.elbigs.controller.cms;

import com.elbigs.dto.FileDto;
import com.elbigs.service.AzureBlobAdapter;
import com.elbigs.util.DateUtil;
import com.elbigs.util.ElbigsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Iterator;

@RestController
@RequestMapping("/v1/cms/file")
public class FileController {

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @Value("${blob.storage-url}")
    String azureStorageUrl;

    @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
    @PostMapping("/upload")
    public FileDto uploadFile(MultipartHttpServletRequest request) {
        Iterator<String> iterator = request.getFileNames();

        FileDto fileDto = new FileDto();

        while (iterator.hasNext()) {
            String uploadFileName = iterator.next();
            MultipartFile mFile = request.getFile(uploadFileName);
            String orgFileName = mFile.getOriginalFilename();

            String ext = orgFileName.substring(orgFileName.length() - 3, orgFileName.length());

            // 프리뷰 이미지 cloud upload
            String dir = DateUtil.getCurrDateStr("yyyyMMdd");
            String convertName = ElbigsUtil.makeRandAlpabet(10) + (System.currentTimeMillis() / 1000);
            String uploadPath = dir + "/" + convertName + "." + ext;
            System.out.println("uploadPath : " + uploadPath);
            azureBlobAdapter.upload(mFile, uploadPath);

            fileDto.setOriginFileName(orgFileName);
            fileDto.setDownloadPath(azureStorageUrl + "/" + uploadPath);
            fileDto.setUploadPath(uploadPath);
            break;
        }

        return fileDto;

    }
}
