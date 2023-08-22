package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.dto.Oauth2UserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 자체 회원가입시 사용하는 회원가입 Api의 로직이다. -> 자체이기 때문에 모든일 다 처리
@Service
@Transactional // 중요작업이므로 트랜잭션 걸어주기
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 회원가입
    public void signUp(UserSignUpDto signUpDto) throws Exception{
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) { // 이메일 중복확인
            log.warn("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .nickname(signUpDto.getNickname())
                .school(signUpDto.getSchool())
                .major(signUpDto.getMajor())
                .studentId(signUpDto.getStudentId())
                .build();

        user.passwordEncode(passwordEncoder); // 비밀번호 암호화
        user.authtorizeAuthing(); // 인증되지 않은 권한으로 변경 -> 나중에 사람이 하나하나 검사하고 인증됨으로 바꿔줄거임.
        //todo: 학생증 사진보고 인증가능하도록 수정.
        userRepository.save(user); // db에 저장
    }

    // update 역할
    public void oauth2SignUp(Oauth2UserSignUpDto userSignupDto,String temp) throws Exception{
        // SignUpDto => 추가 정보.
        // Access 토큰에 들어있는 클레임에 이메일 들어있음
        if(jwtService.extractEmail(temp).isPresent()) {
            if (userRepository.findByEmail(jwtService.extractEmail(temp).get()).isPresent()) {
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get()).get();
                user.setNickname(userSignupDto.getNickname());
                user.setSchool(userSignupDto.getSchool());
                user.setMajor(userSignupDto.getMajor());
                user.setStudentId(userSignupDto.getStudentId());
                user.authtorizeAuthing();
                //todo: 학생증 사진보고 인증가능하도록 수정.
                userRepository.save(user);
            } else {
                log.warn("소셜 회원가입이 되어있지 않습니다.");
            }
        }
        else{
            log.warn("토큰이 올바르지 않습니다.");
        }
    }
}

