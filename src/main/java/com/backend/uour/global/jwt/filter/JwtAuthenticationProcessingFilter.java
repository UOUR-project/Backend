package com.backend.uour.global.jwt.filter;

import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import com.backend.uour.global.jwt.util.PasswordUtil;

import java.io.IOException;

// 기본적으로 refresh 가 넘어오는상황은 access 가 만료되었을때임.
// 토큰을 받는 경우는 3가지 -> 이걸 doFilterInternal 에서 처리 -> 경우에 따라 각각 필터로 뿌려준다.
// 1. refresh 있다 -> DB 의 refresh 와 비교, 맞으면 Access, refresh 둘다 재발급, 아니면 인증실패
// 2. refresh 없고 access 만 있고 유효 -> 인증성공처리
// 3. refresh 없고 access 만 있고 유효하지 않음 -> 인증실패처리 , 403

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // /login으로 들어오는 요청은 토큰 검사를 하지 않음
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)){
            filterChain.doFilter(request, response); // /login으로 들어오면 토큰 검사를 하지 않음
            return; // -> 현재 필터의 흐름을 막는다 ->  여기서 끝!
        }
        String refresh = jwtService.extractRefreshToken(request)// refresh 토큰을 받음 -> 이게 있다? -> 3번 경우
                .filter(jwtService::isTokenValid) // 유효까지 검사하고
                .orElse(null); // 없거나 유효X? -> Null

        if (refresh != null){ // 1번 경우
            // refresh 토큰 재발급 및 db 에 저장
            checkRefreshTokenAndReIssueAccessToken(response, refresh);
            return; // 스프링에서 더이상 필터를 진행하지 않도록 리턴
        }
        else { // 2, 3번 경우
            // 일단 refresh가 아니므로 access 검사를 하러 가는것.
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    // 1번 경우 처리 로직 -> refresh 토큰 재발급 및 db 에 저장
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse res, String refreshToken) throws IOException {
        // refresh로 유저찾기, 있으면 Access 만들어준다 -> 1의 성공, 없으면 인증실패 -> 1의 실패
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String token = reIssueRefreshToken(user); // refresh 토큰 재발급
                    jwtService.sendAccessAndRefreshToken(res, jwtService.createAccessToken(user.getEmail()),token); // access, refresh 토큰을 보내줌
                });
        // 만약 유저를 refresh 로 찾고, 있다면 access 토큰을 만들어서 보내주고, refresh 토큰을 재발급해서 db에 저장해준다.
    }

    // refresh 토큰 재발급 및 db 에 저장
    private String reIssueRefreshToken(User user){
        String reIssuedRefreshToken = jwtService.createRefreshToken(); // refresh 토큰 재발급 (claim 없음)
        user.updateRefreshToken(reIssuedRefreshToken); // repository에 올리고
        userRepository.saveAndFlush(user); // db에 저장
        return reIssuedRefreshToken;

    }
    // 2, 3번 경우 처리 로직 -> access 검사를 하러 가는것.
    public void checkAccessTokenAndAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        jwtService.extractAccessToken(req)
                .filter(jwtService::isTokenValid) // 유효한 토큰인지 검사
                .ifPresent(token -> jwtService.extractEmail(token) // 만약 유효하면 이메일을 뽑는다.
                        .ifPresent(email -> userRepository.findByEmail(email) // 이메일로 유저를 찾는다.
                                .ifPresent(this::saveAuthentication))); // 유저가 있다면 인증처리를 해준다. (토큰 -> 이메일 -> 유저 -> 인증처리)
        filterChain.doFilter(req, res); // 인증처리가 끝나면 다음 필터로 넘어감
    }


    // 인증처리 -> Authentication 객체를 만들어서 SecurityContext 에 넣어준다.
    // Authenticaion 내부에는 UserDetails 타입의 principal 객체가 들어가야함.
    public void saveAuthentication(User user){
        String pw = user.getPassword();
        if (pw == null){ //소셜로그인은 비번이 없음, 임의로 넣어주자
            pw = PasswordUtil.generateRandomPassword();
        }
        // UserDetails 객체 생성
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(pw)
                .roles(user.getRole().name())
                .build();
        // Authentication 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        // SecurityContext 에 Authentication 객체를 넣어줌
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
