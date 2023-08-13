package com.backend.uour.global.login.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

// 로그인 실패 핸들러
// response에 실패 정보 담아주기

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    // 로그인 실패시 404에러와 메시지 띄우기.
    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) throws IOException {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 에러
        res.setCharacterEncoding("utf-8");
        res.setContentType("text/plain; charset=utf-8"); // 응답 데이터 타입
        res.getWriter().write("로그인 실패");
        log.info("로그인 실패, 메시지: {}", exception.getMessage());
    }
}
