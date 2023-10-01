package com.backend.uour.domain.community.entity;

import com.backend.uour.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @OneToMany(mappedBy = "board")
    private Set<Comment> comment;
    @ColumnDefault("0")
    private int view;
    private LocalDateTime WriteTime;
    private LocalDateTime updateTime;

    public void addView(){
        this.view++;
    }

    @Builder
    private Board(String title, String content, User author){
        this.title = title;
        this.content = content;
        this.author = author;
        this.WriteTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
