package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity extends BaseEntity {

    private long id;
    private long connectableId;
    private String connectableType;
    private String convertName;
    private long fileNumber;
    private String fullPath;
    private String mimeType;
    private String originName;
    private String size;
    private String type;

}
