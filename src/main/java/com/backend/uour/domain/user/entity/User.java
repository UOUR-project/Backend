package com.backend.uour.domain.user.entity;

import jakarta.persistence.*;
import com.backend.uour.domain.user.entity.ROLE;
import com.backend.uour.domain.user.entity.SCHOOL;
import com.backend.uour.domain.user.entity.SOCIAL;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자동생성되는 db의 pk
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


    public void authtorizeUnAuth(){ // 권한을 인증되지 않은 권한으로 변경
        this.role = ROLE.UNAUTH;
    }
    public void authtorizeAuth(){ // 권한을 인증된 권한으로 변경
        this.role = ROLE.AUTH;
    }
    public void passwordEncode(PasswordEncoder passwordEncoder){ // 생으로 들어오는 비밀번호 암호화
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String refreshToken){ // 리프레시 토큰 갱신
        this.refreshToken = refreshToken;
    }
}
