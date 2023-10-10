package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class PhotoHandler {
    // 프로젝트 디렉터리 내부의 저장을 위해 절대경로 지정.
    @Value("${absolutePath}")
    private String absolutePath;
    public List<Photo> parseFileInfo(Board board,List<MultipartFile> multipartFiles) throws Exception{
        List<Photo> filelist = new ArrayList<>(); // 일단 반환할 파일 리스트 먼저 생성.

        // Null 일 경우에 에러가 날 수 있으므로 CollectionUtils 를 사용.
        if(!CollectionUtils.isEmpty(multipartFiles)){ // 받아온 파일에 사진이 있을때.
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String currentDate = now.format(dateTimeFormatter);


            // TODO : S3 기반의 저장소로 연동되도록 변경하기.
            // 파일을 저장할 세부경로 지정
            String path = File.separator + currentDate;
            File file = new File(absolutePath + path);

            // 디렉터리가 없으면 생성
            if(!file.exists()){
                boolean suc = file.mkdir();
                if(!suc)
                    System.out.println("file processing fail");
            }
            // 다중 파일 처리
            for(MultipartFile multipartFile : multipartFiles){
                // 파일 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                // 확장자명 없으면 처리 안함.
                if (ObjectUtils.isEmpty(contentType)){
                    break;
                }
                else{
                    // 확장자가 jpeg, png 만 지정.
                    if(contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg"; // 변경해준다.
                    else if(contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else
                        break; // 나머지는 처리 안함.
                }
                // 파일 이름은 나노초 + 확장자. -> 겹칠일이 전혀 없다!
                String newFileName = System.nanoTime() + originalFileExtension;

                // Photo 객체 생성.
                Photo photo = new Photo(
                        multipartFile.getOriginalFilename(),
                        path+File.separator+newFileName,
                        multipartFile.getSize()
                );

                System.out.println(absolutePath + path + File.separator + newFileName);
                if(board.getId() != null){
                    photo.setBoard(board);
                }
                filelist.add(photo); // 생성후 리스트에 추가.
                // 업로드한 파일 데이터를 지정한 파일에 저장.
                file = new File(absolutePath + path + File.separator + newFileName);
                multipartFile.transferTo(file);
                // 파일 권한 설정.
                file.setReadable(true);
                file.setWritable(true);
            }
        }
        return filelist;
    }
}
