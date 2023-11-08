package com.backend.uour.domain.photo.entity;

import com.backend.uour.domain.user.entity.StudentId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Id_Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    private String originalFileName;
    private String filePath;
    private Long fileSize;

    @OneToOne(mappedBy = "Id_photo")
    private StudentId studentId;

    @Builder
    public Id_Photo(String originalFileName, String filePath, Long fileSize, StudentId studentId){
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.studentId = studentId;
    }

}
