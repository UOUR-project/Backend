package com.backend.uour.domain.user.dto;

import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MypageDto {
    private String nickname;
    // 이메일을 보내주는 의미가 있나?
    private String email;
    private SCHOOL school;
    private String major;
}
