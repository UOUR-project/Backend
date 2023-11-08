package com.backend.uour.domain.photo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoDto {
    private String origFileName;
    private String filePath;
    private Long fileSize;

    @Builder
    public PhotoDto(String origFileName, String filePath, Long fileSize){
        this.origFileName = origFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}
