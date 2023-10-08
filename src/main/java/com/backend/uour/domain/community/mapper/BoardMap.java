package com.backend.uour.domain.community.mapper;

import com.backend.uour.domain.community.dto.*;
import com.backend.uour.domain.community.entity.Board;

import com.backend.uour.domain.community.repository.CommentRepository;
import com.backend.uour.domain.community.repository.LikeBoardRepository;
import com.backend.uour.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BoardMap {
    private final LikeBoardRepository likeBoardRepository;
    private final CommentRepository commentRepository;

    // Post mapper
    public Board ToEntity(BoardPostDto dto, User author){
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .category(dto.getCategory())
                .build();
    }

    // Author mapper
    public AuthorDto toAuthorDto(User author){
        return AuthorDto.builder()
                .nickname(author.getNickname())
                .id(author.getId())
                .build();
    }

    // detail mapper
    public BoardDetailDto ToDetailDto(Board board){
        return BoardDetailDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .author(toAuthorDto(board.getAuthor()))
                .category(board.getCategory())
                .updatedTime(board.getUpdateTime())
                .writedTime(board.getWriteTime())
                .views(board.getView())
                .commentsCount(commentRepository.countByBoardId(board.getId()))
                .likes(likeBoardRepository.countByBoardId(board.getId()))
                .build();
    }

    public BoardListDto toListDto(Board board){
        return BoardListDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .likes(likeBoardRepository.countByBoardId(board.getId()))
                .comments(commentRepository.countByBoardId(board.getId()))
                .authorDto(toAuthorDto(board.getAuthor()))
                .views(board.getView())
                .build();
    }

    public BoardListMicroDto toListMicroDto(Board board){
        return BoardListMicroDto.builder()
                .title(board.getTitle())
                .category(board.getCategory())
                .likes(likeBoardRepository.countByBoardId(board.getId()))
                .comments(commentRepository.countByBoardId(board.getId()))
                .views(board.getView())
                .build();
    }
}
