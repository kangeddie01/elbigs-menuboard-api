package com.elbigs.dto;

import com.elbigs.entity.FileEntity;
import com.elbigs.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto extends UserEntity {

    private int totalCount;
    private int shopCount;
    private int active;
    private int inactive;
    private List<FileEntity> files = new ArrayList<>();

}
