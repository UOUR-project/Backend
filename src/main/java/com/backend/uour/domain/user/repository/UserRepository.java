package com.backend.uour.domain.user.repository;

import com.backend.uour.domain.user.entity.SOCIAL;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByRefreshToken(String refreshToken);

    // 나중에 소셜과 일반 로그인 구분할때 사용
    // 추가정보를 받을때 사용
    Optional<User> findByEmailAndSocialId(String email, String socialId);

    // OAuth2 로그인, 회원가입 구분
    Optional<User> findBySocialTypeAndSocialId(SOCIAL social,String socialid);

}
