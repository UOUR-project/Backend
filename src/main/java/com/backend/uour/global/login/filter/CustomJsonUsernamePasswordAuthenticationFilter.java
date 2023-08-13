package com.backend.uour.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// UsernamePasswordAuthenticationFilter -> 스프링 기본 로그인폼.
// 거의 구조가 비슷하지만 타입만 JSON으로 만들려고한다.
public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login"; // "/login"으로 오는 요청을 처리
    private static final String HTTP_METHOD = "POST"; // 로그인 HTTP 메소드는 POST
    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private static final String USERNAME_KEY = "email"; // 회원 로그인 시 이메일 요청 JSON Key : "email"
    private static final String PASSWORD_KEY = "password"; // 회원 로그인 시 비밀번호 요청 JSon Key : "password"
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // "/login" + POST로 온 요청에 매칭된다.

    private final ObjectMapper objectMapper;// JSON 데이터를 객체로 변환하기 위한 ObjectMapper

    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 기본 생성자
        this.objectMapper = objectMapper; // ObjectMapper 주입
    }

    // 인증처리
    // 동일하게 UsernamePasswordAuthenticationToken을 반환한다. -> StreamUtils.copyToString()을 이용해서 request에서 messageBody 반환.
    // 이후 ObjectMapper를 이용해서 JSON 데이터를 객체로 변환한다. -> token의 principal, credentials에 이메일, 비번 넣는다.
    // AuthenticationManager의 authenticate() 에 객체를 넣고 인증처리.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        // json만 사용
        if (request.getContentType()== null|| !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Content type must be application/json");
        }
        // json 데이터를 객체로 변환
        String messegeBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String,String> usernamePassword = objectMapper.readValue(messegeBody, Map.class);

        String email = usernamePassword.get(USERNAME_KEY);
        String password = usernamePassword.get(PASSWORD_KEY);

        // UsernamePasswordAuthenticationToken 생성 -> AuthenticationManager에서 인증시 사용할 인증대상.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
