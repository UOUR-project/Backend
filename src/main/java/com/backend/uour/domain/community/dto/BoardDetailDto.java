package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.CATEGORY;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
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
    private List<Long> photoId;
    private List<CommentListDto> comments;
    private LocalDateTime updatedTime;
    private LocalDateTime writedTime;
    private CATEGORY category;
}
