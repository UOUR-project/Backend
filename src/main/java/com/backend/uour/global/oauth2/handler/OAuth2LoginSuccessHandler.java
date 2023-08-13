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
                res.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken); // accessToken 헤더에 추가
                res.sendRedirect("/api/user/sign-up"); // 회원가입 페이지로 (여기서 받을 정보들은 추후에) 리다이렉트
                //todo: 추가회원가입때 받아야할 내용들. 이 아래 두줄 없애야한다.
                jwtService.sendAccessAndRefreshToken(res, accessToken, null);
                User user = userRepository.findByEmail(oAuth2User.getEmail()).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
                user.authtorizeAuth(); // 권한 부여
            }
//            else if (oAuth2User.getRole() == ROLE.AUTHING) {
//                //todo : 회원가입 대기중일때 로그인 시도하면 어쩌지?
//            }
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
        res.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken); // accessToken 헤더에 추가
        res.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken); // refreshToken 헤더에 추가
        jwtService.sendAccessAndRefreshToken(res, accessToken, refreshToken); // accessToken, refreshToken 쿠키에 추가
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken); // refreshToken 업데이트
    }
}
