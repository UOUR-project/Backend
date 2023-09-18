package com.backend.uour.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IdImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user")
    private User user; // user의 pk

    private String originalname;

    @Column(unique = true)
    private String filename;

    private String url;

    @Builder
    public IdImage(User user, String originalname, String filename){
        this.user = user;
        this.originalname = originalname;
        this.filename = filename;
    }
}
