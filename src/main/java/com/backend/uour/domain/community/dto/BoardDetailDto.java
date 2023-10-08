package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.CATEGORY;
import com.backend.uour.domain.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
// 게시글 디테일 DTO
public class BoardDetailDto {
    private String title;
    private String content;
    private int views;
    private int likes;
    private AuthorDto author;
    private int commentsCount;
    private LocalDateTime updatedTime;
    private LocalDateTime writedTime;
    private CATEGORY category;
}
