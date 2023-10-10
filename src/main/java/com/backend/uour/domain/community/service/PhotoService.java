package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.dto.PhotoDto;
import com.backend.uour.domain.community.dto.PhotoResponseDto;
import com.backend.uour.domain.community.entity.Photo;
import com.backend.uour.domain.community.repository.PhotoRepository;
import com.backend.uour.global.exception.NoPhotoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

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
    public List<PhotoResponseDto> findAllByBoard(Long boardId){
        List<Photo> photoList = photoRepository.findAllByBoardId(boardId);

        return photoList.stream()
                .map(PhotoResponseDto::new)
                .collect(Collectors.toList());
    }
    public void deletePhoto (Photo photo){
        photoRepository.delete(photo);
    }
}
