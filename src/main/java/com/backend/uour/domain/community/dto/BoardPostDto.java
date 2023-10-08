package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.CATEGORY;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 올리거나 수정하는 DTO
@Getter
@NoArgsConstructor
public class BoardPostDto {
    private CATEGORY category;
    private String title;
    private String content;
}
