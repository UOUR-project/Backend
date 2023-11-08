package com.backend.uour.domain.photo.dto;

import com.backend.uour.domain.photo.entity.Photo;
import lombok.Getter;

@Getter
public class PhotoResponseDto {
    private Long fileId;


    public PhotoResponseDto(Photo photo){
        this.fileId = photo.getId();
    }
}
