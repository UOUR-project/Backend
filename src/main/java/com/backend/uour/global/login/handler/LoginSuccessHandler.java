package com.backend.uour.global.login.handler;

import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

// Json 로그인 성공시 핸들러 -> SimpleUrlAuthenticationSuccessHandler 를 상속받아서 구현
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Value("${jwt.access.expiration}")
    private String accessExpiry;


    // 로그인 성공시 처리 시나리오, -> 성공을 가정 하기때문에 Access, refresh를 같이 만들어서 response에 넣어준다.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication){
        String email = extractUsername(authentication);
        String access = jwtService.createAccessToken(email); // access 토큰 생성
        String refresh = jwtService.createRefreshToken(); // refresh 토큰 생성

        // refresh 토큰을 DB에 저장
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refresh);
                    userRepository.saveAndFlush(user);
                });
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", access);
        log.info("발급된 AccessToken 만료 기간 : {}", accessExpiry);
    }
    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
