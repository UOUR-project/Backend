package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.entity.IdImage;
import com.backend.uour.domain.user.repository.IdImageRepository;
import com.backend.uour.global.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class IdImageService {
    private final IdImageRepository idImageRepository;
    private final S3Uploader s3Uploader;

    public Long saveIdImage(MultipartFile image, IdImage idimage) throws IOException {
        if (!image.isEmpty()){
            String storedFileName = s3Uploader.upload(image,"idImage"); // S3에 업로드된 파일의 URL을 반환
            idimage.setUrl(storedFileName);
        }
        IdImage savedIdImage = idImageRepository.save(idimage);
        return savedIdImage.getId();
    }
}
