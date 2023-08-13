package com.backend.uour.domain.user.dto;


import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//자체 로그인할때 받을것들 -> 추가정보까지 받는다.

@Getter
@NoArgsConstructor
public class UserSignUpDto {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private SCHOOL school; // -> 추가정보
    private String major; // -> 추가정보
    private String studentId; // -> 추가정보
}
