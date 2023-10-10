package com.backend.uour.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class CommentListDto {
    private String content;
    private LocalDateTime WriteTime;
    private LocalDateTime updateTime;
    private AuthorDto author;
    private boolean isReply;
    private Long commentGroup;
    private int likes;
}
