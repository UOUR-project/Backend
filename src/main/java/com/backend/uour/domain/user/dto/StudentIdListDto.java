package com.backend.uour.domain.user.dto;

import com.backend.uour.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Getter
@Builder
public class StudentIdListDto {
    private StudentDto user;
    private Long id;
    private String Id_photo;
}
