package com.backend.uour.domain.photo.service;

import com.backend.uour.domain.photo.dto.Id_PhotoDto;
import com.backend.uour.domain.photo.dto.PhotoDto;
import com.backend.uour.domain.photo.dto.PhotoResponseDto;
import com.backend.uour.domain.photo.entity.Id_Photo;
import com.backend.uour.domain.photo.entity.Photo;

import java.util.List;

public interface PhotoService {
    PhotoDto findByFileId(Long fileId) throws Exception;
    Id_PhotoDto findById_PhotoId(Long fileId) throws Exception;
    List<PhotoResponseDto> findAllByBoard(Long boardId);
    void deletePhoto (Photo photo) throws Exception;
    void deleteId_Photo (Id_Photo photo) throws Exception;
}
