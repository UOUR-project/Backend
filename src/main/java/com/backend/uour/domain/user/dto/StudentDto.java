package com.backend.uour.domain.user.dto;

import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StudentDto {
    private SCHOOL school; // -> 추가정보
    private String major; // -> 추가정보
    private String studentId; // -> 추가정보
    private Long id;
}
