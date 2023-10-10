package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PhotoResponseDto {
    private Long fileId;


    public PhotoResponseDto(Photo photo){
        this.fileId = photo.getId();
    }
}
