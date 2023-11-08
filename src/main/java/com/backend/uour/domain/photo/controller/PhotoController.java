package com.backend.uour.domain.photo.controller;

import com.backend.uour.domain.photo.dto.Id_PhotoDto;
import com.backend.uour.domain.photo.dto.PhotoDto;
import com.backend.uour.domain.photo.service.PhotoService;
import com.backend.uour.global.network.ResultDTO;
import com.backend.uour.global.network.STATUS;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.InputStream;

@RequiredArgsConstructor
@RestController
public class PhotoController {
    private final PhotoService photoService;
    @Value("${absolutePath}")
    private String absolutePath;
    // 이미지 단건 조회 (썸네일, 일반)
    // todo : 썸네일용으로 사이즈 줄이기
    @GetMapping(value = "/Image/{id}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<?> getImage(@PathVariable Long id) throws Exception {
        try {
            PhotoDto photodto = photoService.findByFileId(id);
            String path = photodto.getFilePath();
            InputStream imageStream = new FileInputStream(absolutePath + path);
            byte[] imageByteArray = IOUtils.toByteArray(imageStream);
            imageStream.close();
            return ResponseEntity.ok(imageByteArray);
        } catch (Exception E) {
            // 컨텐츠 타입 정해져 있음. -> ResultDTO X
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping(value = "/IdImage/{id}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<?> getId_Image(@PathVariable Long id) throws Exception {
        try {
            Id_PhotoDto photodto = photoService.findById_PhotoId(id);
            String path = photodto.getFilePath();
            InputStream imageStream = new FileInputStream(absolutePath + path);
            byte[] imageByteArray = IOUtils.toByteArray(imageStream);
            imageStream.close();
            return ResponseEntity.ok(imageByteArray);
        } catch (Exception E) {
            // 컨텐츠 타입 정해져 있음. -> ResultDTO X
            return ResponseEntity.badRequest().body(null);
        }
    }

}