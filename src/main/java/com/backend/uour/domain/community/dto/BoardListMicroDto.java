package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.CATEGORY;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
// 홈화면에서의 미니 게시판
public class BoardListMicroDto {
    private Long id;
    private String title;
    private int views;
    private int likes;
    private int comments;
    private CATEGORY category;
}
// todo: micromicro -> 제목이랑 아이디만.