package com.elbigs.dto;

import com.elbigs.entity.MediaLibEntity;
import lombok.Data;

@Data
public class MediaLibDto extends MediaLibEntity {
    private String mediaCategoryName;


    private String sizeTxt;

    public String getSizeTxt() {
        Long size = this.getSize();

        if(size==null){
            return "정보없음";
        }
        String sizeTxt = "";
        if (size < 1024) {
            sizeTxt = size + " Bytes";
        } else if (size / 1024 < 1024) {
            sizeTxt = size / 1024 + " KB";
        } else if (size / 1024 / 1024 < 1024) {
            sizeTxt = size / 1024 / 1024 + " MB";
        }

        return sizeTxt;
    }
}
