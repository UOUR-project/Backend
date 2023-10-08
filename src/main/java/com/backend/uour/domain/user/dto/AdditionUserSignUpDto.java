package com.backend.uour.domain.user.dto;

import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdditionUserSignUpDto {
    private String nickname;
    private String name;
    private SCHOOL school; // -> 추가정보
    private String major; // -> 추가정보
    private String studentId; // -> 추가정보
}
