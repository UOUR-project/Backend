package com.backend.uour.domain.photo.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.net.URLDecoder;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /* 1. 파일 업로드 */
    public String upload(MultipartFile multipartFile, String s3FileName) throws IOException {
        // 메타데이터 생성
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());
        // putObject(버킷명, 파일명, 파일데이터, 메타데이터)로 S3에 객체 등록
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);
        // 등록된 객체의 url 반환 (decoder: url 안의 한글or특수문자 깨짐 방지)
        return URLDecoder.decode(amazonS3.getUrl(bucket, s3FileName).toString(), "utf-8");
    }

    /* 2. 파일 삭제 */
    public void delete (String keyName) {
        try {
            // deleteObject(버킷명, 키값)으로 객체 삭제
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.toString());
        }
    }
}
