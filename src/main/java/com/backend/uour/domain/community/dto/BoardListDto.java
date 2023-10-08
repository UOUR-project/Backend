package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.CATEGORY;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
// 카테고리별 게시판 보여줄때 필요한 DTO
public class BoardListDto {
    private String title;
    private String content;
    private AuthorDto authorDto;
    private int views;
    private int likes;
    private int comments;
    private CATEGORY category;
}
