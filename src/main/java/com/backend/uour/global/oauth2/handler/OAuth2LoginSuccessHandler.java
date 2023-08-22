package com.backend.uour.global.oauth2.handler;

import com.backend.uour.domain.user.entity.ROLE;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // role 이 UNAUTH 면 이제 막 회원가입한 것.
            if (oAuth2User.getRole() == ROLE.UNAUTH) {
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail()); // accessToken 생성
                jwtService.sendAccessToken(res, accessToken);
                res.sendRedirect("oauth2/sign-up");
            }
            else {
                loginSuccess(res, oAuth2User);
            }
        }
        catch (Exception e){
            throw e;
        }
    }
    // 로그인 성공시 access, refresh 둘다 업데이트
    private void loginSuccess(HttpServletResponse res, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail()); // accessToken 생성
        String refreshToken = jwtService.createRefreshToken(); // refreshToken 생성
        jwtService.sendAccessAndRefreshToken(res, accessToken, refreshToken); // accessToken, refreshToken 쿠키에 추가
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken); // refreshToken 업데이트
        userRepository.findByEmail(oAuth2User.getEmail())
                .ifPresent(user -> {
                    user.updateRefreshToken("Bearer " + refreshToken);
                    userRepository.saveAndFlush(user);
                });
    } // 로그인 성공시 access, refresh 둘다 업데이트 (refreshToken은 DB에도 업데이트)
}
