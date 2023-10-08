package com.backend.uour.domain.user.dto;

import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 수정 가능한 것들 리스트
public class UserUpdateDto {
    private String nickname;
    // todo: 이메일이 수정되면 이메일 인증을 다시 받아야한다.
    private String email;

    // todo: 학교 관련 코드가 수정되면 학생증 인증을 다시 받야야 하지 않을까?
    private SCHOOL school;
    private String major;
    private String studentId;

    // 기존 번호와 현재 번호가 일치하면 새 비밀번호를 넣는식.
    private String password;
    private String newPassword;
}
