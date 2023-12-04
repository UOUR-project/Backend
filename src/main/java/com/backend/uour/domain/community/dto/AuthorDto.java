package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class AuthorDto {
    private String nickname;
    private SCHOOL school;
    private Long id;
}
