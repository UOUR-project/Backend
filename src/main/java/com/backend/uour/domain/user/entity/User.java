package com.backend.uour.domain.user.entity;

import com.backend.uour.domain.community.entity.LikeBoard;
import com.backend.uour.domain.community.entity.Scrap;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자동생성되는 db의 pk
    @Column(unique = true)
    private String email;
    private String password;
    private String nickname;

    private String name;
    @Enumerated(EnumType.STRING)
    private ROLE role;
    private String refreshToken; // 리프레시 토큰

    // 소셜 로그인 추가정보
    private String socialId;
    @Enumerated(EnumType.STRING)
    private SOCIAL socialType;

    // 회원가입후 추가
    @Enumerated(EnumType.STRING)
    private SCHOOL school; // -> 추가정보
    private String major; // -> 추가정보
    private String studentId; // -> 추가정보

    @OneToMany(mappedBy = "pointed")
    private List<Blame> blames = new ArrayList<>();
    // 좋아요, 스크랩
    @OneToMany(mappedBy = "user")
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Scrap> scrapBoards = new ArrayList<>();

    public void authtorizeUnAuth(){ // 권한을 인증되지 않은 권한으로 변경
        this.role = ROLE.UNAUTH;
    }
    public void authtorizeAuth(){ // 권한을 인증된 권한으로 변경
        this.role = ROLE.AUTH;
    }
    public void authtorizeAdmin(){
        this.role = ROLE.ADMIN;
    }
    public void authtorizeAuthing(){
        this.role = ROLE.AUTHING;
    }
    public void passwordEncode(PasswordEncoder passwordEncoder){ // 생으로 들어오는 비밀번호 암호화
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String refreshToken){ // 리프레시 토큰 갱신
        this.refreshToken = refreshToken;
    }
    public void addLikeBoard(LikeBoard likeBoard){
        this.likeBoards.add(likeBoard);
        if (likeBoard.getUser() != this)
            likeBoard.setUser(this);
    }
    public void addScrapBoard(Scrap scrap){
        this.scrapBoards.add(scrap);
        if (scrap.getUser() != this)
            scrap.setUser(this);
    }

}
