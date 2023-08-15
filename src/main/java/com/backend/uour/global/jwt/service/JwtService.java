package com.backend.uour.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.backend.uour.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {
    private final UserRepository userRepository;
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}") // application-jwt.yml 에서 설정한 값들을 가져옴.
    private String refreshHeader;
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    // Access Token 생성
    // claim에 이메일 넣기
    public String createAccessToken(String email){
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshExpiration))
                .withClaim(EMAIL_CLAIM,email)
                .sign(Algorithm.HMAC512(secretKey));
    }
    // Refresh Token 생성
    // claim에 이메일조차 없이 ㄱㄱ
    public String createRefreshToken(){
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshExpiration))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // 응답 Access Token 헤더에 넣어주기
    public void sendAccessToken(HttpServletResponse res, String accessToken){
        res.setStatus(HttpServletResponse.SC_OK); // 200
        res.setHeader(accessHeader, accessToken); // 헤더에 accessToken 넣어주기
        log.info("Access 재발급완료 : {}", accessToken);
    }

    // 응답 Refresh Token 헤더에 넣어주기 -> 만료시 -> 이경우에는 AccessToken도 재발급해줘야함.
    public void sendAccessAndRefreshToken(HttpServletResponse res, String accessToken, String refreshToken){
        res.setStatus(HttpServletResponse.SC_OK); // 200
        res.setHeader(accessHeader, accessToken); // 헤더에 accessToken 넣어주기
        res.setHeader(refreshHeader, refreshToken); // 헤더에 refreshToken 넣어주기
        log.info("Refresh 재발급완료: {}, Access 재발급완료: {}", refreshToken, accessToken);
    }

    // 요청 헤더에서 Refresh Token 추출
    public Optional<String> extractRefreshToken(HttpServletRequest req){
        return Optional.ofNullable(req.getHeader(refreshHeader)) // 헤더에서 refreshHeader를 가져옴
                .filter(refresh -> refresh.startsWith(BEARER)) // refreshHeader가 BEARER로 시작하는지 확인
                .map(refresh -> refresh.replace(BEARER, "")); // BEARER를 ""로 바꿔줌 -> 토큰만 추출
    }
    //요청 헤더에서 Access Token 추출
    public Optional<String> extractAccessToken(HttpServletRequest req){
        return Optional.ofNullable(req.getHeader(accessHeader)) // 헤더에서 accessHeader를 가져옴
                .filter(access -> access.startsWith(BEARER)) // accessHeader가 BEARER로 시작하는지 확인
                .map(access -> access.replace(BEARER, "")); // BEARER를 ""로 바꿔줌 -> 토큰만 추출
    }

    // Access Token에서 이메일 추출
    // 순서 -> JWT.require()로 검증, verify()에서 Access Token 검증, 유효하면 getClaim으로 이메일 추출
    public Optional<String> extractEmail(String accessToken){
        try{
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey)) // 유효성 검사 알고리즘있는 JWT verifier builder 반환
                    .build() //받은 빌더로 JWT verifier 생성
                    .verify(accessToken) // Access Token 검증
                    .getClaim(EMAIL_CLAIM) // EMAIL_CLAIM을 통해 이메일 추출
                    .asString());
        }catch (Exception e){ // 검증 실패시
            log.error("Access Token이 유효하지 않습니다.");
            return Optional.empty();
        }
    }
    //AccessToken 헤더 설정
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }


    // RefreshToken 헤더 설정
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    // Refresh Token DB 저장 -> Refresh Token은 DB에 저장해야함.
    // 회원가입시 유저 entity에는 Refresh Token이 없음. -> 로그인시에 Refresh Token을 생성하고 DB에 저장해야함.
    // LoginSuccessHandler에서 이 메소드를 호출
    public void updateRefreshToken(String email ,String refreshToken) {
        // DB에 저장
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken)
                        , () -> log.error("존재하지 않는 회원입니다."));
    }

    // 유효한 토큰인가요?
    public boolean isTokenValid(String token){
        try{
            JWT.require(Algorithm.HMAC512(secretKey)) // 토큰검사하는 JWT verifier builder 반환
                    .build()
                    .verify(token); // 토큰 검증
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
