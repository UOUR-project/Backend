package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 자체 회원가입시 사용하는 회원가입 Api의 로직이다. -> 자체이기 때문에 모든일 다 처리
@Service
@Transactional // 중요작업이므로 트랜잭션 걸어주기
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signUp(UserSignUpDto signUpDto) throws Exception{
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) { // 이메일 중복확인
            throw new Exception("이미 가입된 이메일입니다.");
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
        user.authtorizeUnAuth(); // 인증되지 않은 권한으로 변경 -> 나중에 사람이 하나하나 검사하고 인증됨으로 바꿔줄거임.
        //todo: 학생증 사진보고 인증가능하도록 수정.
        userRepository.save(user); // db에 저장
    }
}
