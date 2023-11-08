package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.dto.AdditionUserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.dto.UserUpdateDto;
import com.backend.uour.domain.user.entity.BLAME_CATEGORY;
import com.backend.uour.domain.user.entity.Blame;
import com.backend.uour.domain.user.entity.ROLE;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.BlameRepository;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.exception.NoUserException;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional // 중요작업이므로 트랜잭션 걸어주기
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BlameRepository blameRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 회원가입
    @Override
    public void signUp(UserSignUpDto signUpDto) throws Exception{
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) { // 이메일 중복확인
            log.warn("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .nickname(null)
                .school(null)
                .major(null)
                .studentId(null)
                .build();

        user.passwordEncode(passwordEncoder); // 비밀번호 암호화
        user.authtorizeUnAuth(); // 인증되지 않은 권한으로 변경 -> 나중에 사람이 하나하나 검사하고 인증됨으로 바꿔줄거임.
        userRepository.save(user); // db에 저장
    }

    // update 역할
    @Override
    public void additionSignUp(AdditionUserSignUpDto userSignupDto, String temp) throws Exception{
        // SignUpDto => 추가 정보.
        // Access 토큰에 들어있는 클레임에 이메일 들어있음
        if(jwtService.extractEmail(temp).isPresent()) {
            if (userRepository.findByEmail(jwtService.extractEmail(temp).get()).isPresent()) {
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get()).get();
                user.setNickname(userSignupDto.getNickname());
                user.setSchool(userSignupDto.getSchool());
                user.setMajor(userSignupDto.getMajor());
                user.setStudentId(userSignupDto.getStudentId());
                user.setName(userSignupDto.getName());
                user.authtorizeAuthing();
                userRepository.save(user);
            } else {
                log.warn("회원가입이 되어있지 않습니다.");
            }
        }
        else{
            log.warn("토큰이 올바르지 않습니다.");
        }
    }
    @Override
    public void deleteUser(String temp) throws Exception{
        if(jwtService.extractEmail(temp).isPresent()){
            // 일단 동일하지만 로직 나누기.
            // 소셜 로그인 삭제 절차
            if(jwtService.extractEmail(temp).get().endsWith("socialUser.com")){
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get()).get();
                userRepository.delete(user);
            }
            // 자체 로그인 삭제 절차
            else{
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get()).get();
                userRepository.delete(user);
            }
        }
        else{
            log.warn("토큰이 올바르지 않습니다.");
        }
    }
    @Override
    public void updateUser(UserUpdateDto updateDto, String temp) throws Exception{
        // 확인 절차.
        if (jwtService.extractEmail(temp).isPresent()) {
            // 소셜 로그인 수정 절차
            if (jwtService.extractEmail(temp).get().endsWith("socialUser.com")) {
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get())
                        .orElseThrow(() -> new Exception("소셜 회원가입이 되어있지 않습니다."));
                user.setNickname(updateDto.getNickname());
                user.setSchool(updateDto.getSchool());
                user.setMajor(updateDto.getMajor());
                user.setStudentId(updateDto.getStudentId());
                System.out.println("소셜 진행");
            }
            else {
                // 자체 로그인 수정 절차
                User user = userRepository.findByEmail(jwtService.extractEmail(temp).get())
                        .orElseThrow(() -> new Exception("자체 회원가입이 되어있지 않습니다."));
                if(passwordEncoder.matches(updateDto.getPassword(),user.getPassword())){
                    user.setNickname(updateDto.getNickname());
                    user.setEmail(updateDto.getEmail());
                    user.setSchool(updateDto.getSchool());
                    user.setMajor(updateDto.getMajor());
                    user.setStudentId(updateDto.getStudentId());
                    user.setPassword(updateDto.getNewPassword());
                    System.out.println("로컬 진행");
                }
                else{
                    log.warn("비밀번호가 일치하지 않습니다.");
                }
            }
        }
        else {
            log.warn("토큰이 올바르지 않습니다.");
        }
    }

    @Override
    public boolean blame(Long pointedId, String authorization, BLAME_CATEGORY blameCategory) throws Exception {
        User user = userRepository.findByEmail(jwtService.extractEmail(authorization)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);

        Optional<Blame> temp = blameRepository.findByUserIdAndPointedId(user.getId(),pointedId);
        if (temp.isEmpty()){
            Blame blame = Blame.builder()
                    .user(user)
                    .pointed(userRepository.findById(pointedId)
                            .orElseThrow(NoUserException::new))
                    .blameCategory(blameCategory)
                    .build();
            blameRepository.save(blame);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void promoteAdmin(Long pointedId, String authorization) throws Exception {
        User user = userRepository.findByEmail(jwtService.extractEmail(authorization)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        if(user.getRole().equals(ROLE.ADMIN)){
//        if(user.getRole().equals(ROLE.AUTHING)){
            User pointed = userRepository.findById(pointedId)
                    .orElseThrow(NoUserException::new);
            pointed.authtorizeAdmin();
        }
        else{
            log.warn("관리자 권한이 없습니다.");
        }
    }
}


