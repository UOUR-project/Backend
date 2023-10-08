package com.backend.uour.domain.community.entity;

import com.backend.uour.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


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
    private List<LikeBoard> likeBoards;

    @OneToMany(mappedBy = "board")
    private List<Scrap> scrapBoards;

    @ColumnDefault("0")
    private int view;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;

    private CATEGORY category;

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
}
