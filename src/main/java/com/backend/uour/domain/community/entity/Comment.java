package com.backend.uour.domain.community.entity;

import com.backend.uour.domain.community.mapper.BooleanToYNConverter;
import com.backend.uour.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Date;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    private LocalDateTime WriteTime;
    private LocalDateTime updateTime;
    @ManyToOne
    private User author;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    //대댓글
    private Long commentGroup;

    @Builder
    public Comment(String content, User author, Board board, Long commentGroup){
        this.content = content;
        this.WriteTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.author = author;
        this.board = board;
        this.commentGroup = commentGroup;
    }
}
