package com.backend.uour.domain.photo.service;

import com.backend.uour.domain.photo.dto.Id_PhotoDto;
import com.backend.uour.domain.photo.dto.PhotoDto;
import com.backend.uour.domain.photo.dto.PhotoResponseDto;
import com.backend.uour.domain.photo.entity.Id_Photo;
import com.backend.uour.domain.photo.entity.Photo;
import com.backend.uour.domain.photo.repository.Id_PhotoRepository;
import com.backend.uour.domain.photo.repository.PhotoRepository;
import com.backend.uour.global.exception.NoPhotoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class S3PhotoService implements PhotoService{
    private final S3Service s3Service;
    private final PhotoRepository photoRepository;
    private final Id_PhotoRepository Id_photoRepository;

    @Transactional(readOnly = true)
    public PhotoDto findByFileId(Long fileId) throws Exception {
        Photo entity = photoRepository.findById(fileId).orElseThrow(NoPhotoException::new);
        return PhotoDto.builder()
                .origFileName(entity.getOriginalFileName())
                .filePath(entity.getFilePath())
                .fileSize(entity.getFileSize())
                .build();
    }
    @Transactional(readOnly = true)
    public Id_PhotoDto findById_PhotoId(Long fileId) throws Exception {
        Id_Photo entity = Id_photoRepository.findById(fileId).orElseThrow(NoPhotoException::new);
        return Id_PhotoDto.builder()
                .origFileName(entity.getOriginalFileName())
                .filePath(entity.getFilePath())
                .fileSize(entity.getFileSize())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PhotoResponseDto> findAllByBoard(Long boardId){
        List<Photo> photoList = photoRepository.findAllByBoardId(boardId);

        return photoList.stream()
                .map(PhotoResponseDto::new)
                .collect(Collectors.toList());
    }
    public void deletePhoto (Photo photo) throws Exception{
        try {
            s3Service.delete(photo.getFilePath());
            photoRepository.delete(photo);
        }
        catch (Exception e) {
            throw new Exception();
        }
    }
    public void deleteId_Photo (Id_Photo photo) throws Exception{
        try {
            s3Service.delete(photo.getFilePath());
            Id_photoRepository.delete(photo);
        }
        catch (Exception e) {
            throw new Exception();
        }
    }
}
