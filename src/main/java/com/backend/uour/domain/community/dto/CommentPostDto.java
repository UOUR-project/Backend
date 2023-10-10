package com.backend.uour.domain.community.dto;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class CommentPostDto {
    private String content;
    private LocalDateTime WriteTime;
    private LocalDateTime updateTime;
    private User author;
    private Board board;
    private boolean isReply;
    private Long commentGroup;

}
