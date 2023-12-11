package com.backend.uour.domain.community.entity;

import com.backend.uour.domain.photo.entity.Photo;
import com.backend.uour.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;


    @ColumnDefault("0")
    private int view;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;

    private CATEGORY category;

    @OneToMany(mappedBy = "board")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments = new ArrayList<>();

    // 사진 업로드 관련
    @OneToMany(mappedBy = "board")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Photo> photo = new ArrayList<>();

    public void addView(){
        this.view++;
    }

    public void updateUpdateTime(){
        this.updateTime = LocalDateTime.now();
    }

    @Builder
    private Board(String title, String content, User author,CATEGORY category){
        this.title = title;
        this.content = content;
        this.author = author;
        this.writeTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.category = category;
    }
    public void addPhoto(Photo photo){
        this.photo.add(photo); // Board에서의 파일 처리를 위해
        if(photo.getBoard() != this) // 만약 파일에 게시글 정보가 안들어 있다면
            photo.setBoard(this);  // 해당 파일에 게시판 정보 저장.
    }
    public void addComment(Comment comment){
        this.comments.add(comment);
        if(comment.getBoard() != this)
            comment.setBoard(this);
    }
}
