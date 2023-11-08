package com.backend.uour.domain.photo.entity;

import com.backend.uour.domain.community.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    private Board board;


    private String originalFileName;
    private String filePath;
    private Long fileSize;

    @Builder
    public Photo(String originalFileName, String filePath, Long fileSize){
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public void setBoard(Board board){
        this.board = board;
        if(!board.getPhoto().contains(this))
            board.getPhoto().add(this); // -> 중복 더하기 제한
    }
}
