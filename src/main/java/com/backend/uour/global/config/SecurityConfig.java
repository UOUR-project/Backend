package com.backend.uour.global.config;

import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.backend.uour.global.login.handler.LoginFailureHandler;
import com.backend.uour.global.login.handler.LoginSuccessHandler;
import com.backend.uour.global.login.service.LoginService;
import com.backend.uour.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.backend.uour.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.backend.uour.global.oauth2.service.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.uour.global.jwt.filter.JwtAuthenticationProcessingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import jakarta.servlet.DispatcherType;
import org.springframework.web.filter.CorsFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    // 설정에서 사용할 빈들 가져오기
    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CorsFilter corsFilter;

    // 필터를 만들어보자
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 사용하지 않음 -> 자체로그인
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 사용하지 않음 -> Bearer 방식 사용
                .csrf(AbstractHttpConfigurer::disable) // csrf 사용하지 않음 -> jwt 토큰 사용
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음 -> jwt 토큰 사용
                .addFilter(corsFilter) // corsFilter 등록
                .authorizeHttpRequests(ar -> ar // 요청에 대한 인가 설정
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // forward 요청은 모두 허용 -> forward 요청은 서버 내부에서 다른 서블릿이나 JSP를 호출할 때 사용하는 방식이다.
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/sign-up")).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(ol -> ol
                        .successHandler(oAuth2LoginSuccessHandler) // oauth2 로그인 성공시 후처리
                        .failureHandler(oAuth2LoginFailureHandler) // oauth2 로그인 실패시 후처리
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                            .userService(customOAuth2UserService))); // customService 설정

        // 필터 추가 (순서대로 필터가 실행된다.)
        // 순서 -> logoutFilter -> jwtAuthenticationProcessingFilter -> customJsonUsernamePasswordAuthenticationFilter
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // AuthenticationManager 설정 후 등록. -> DaoAuthenticationProvider 에서 사용
    // DaoAuthenticationProvider에 UserDetailsService 를 loginService로,
    // PasswordEncoder 를 passwordEncoder로 설정해준다.
    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(loginService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler(jwtService,userRepository);
    }
    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    // 커스텀 Json 로그인 필터
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter(){
        CustomJsonUsernamePasswordAuthenticationFilter C_filter = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        C_filter.setAuthenticationManager(authenticationManager());
        C_filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        C_filter.setAuthenticationFailureHandler(loginFailureHandler());
        return C_filter;
    }

    @Bean
    // JWT 인증필터
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() throws Exception{
        return new JwtAuthenticationProcessingFilter(jwtService,userRepository);
    }


}
