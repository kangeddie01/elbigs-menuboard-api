package com.elbigs.dto;

import lombok.Data;

@Data
public class FileDto {

    private String originFileName;
    private String uploadPath;
    private String downloadPath;
    private long fileSize;
}
