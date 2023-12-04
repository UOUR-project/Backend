package com.backend.uour.domain.community.mapper;

import com.backend.uour.domain.community.dto.*;
import com.backend.uour.domain.community.entity.Board;

import com.backend.uour.domain.community.repository.CommentRepository;
import com.backend.uour.domain.community.repository.LikeBoardRepository;
import com.backend.uour.domain.community.repository.ScrapRepository;
import com.backend.uour.domain.photo.repository.PhotoRepository;
import com.backend.uour.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardMap {
    private final LikeBoardRepository likeBoardRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final CommentMap commentMap;
    private final PhotoRepository photoRepository;

    // Post mapper
    public Board ToEntity(BoardPostDto dto, User author) throws Exception {
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
                .school(author.getSchool())
                .id(author.getId())
                .build();
    }

    // detail mapper
    public BoardDetailDto ToDetailDto(Board board, List<Long> photoId, User visitor){
        List<String> PhotoList = photoId.stream().map(id -> photoRepository.findById(id).get().getFilePath()).toList();
        if (visitor == null){
            return BoardDetailDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .author(toAuthorDto(board.getAuthor()))
                    .category(board.getCategory())
                    .updatedTime(board.getUpdateTime())
                    .writedTime(board.getWriteTime())
                    .photoId(PhotoList)
                    .comments(commentRepository.findByBoardId(board.getId()).stream().map(commentMap::tolistDto).collect(Collectors.toList()))
                    .views(board.getView())
                    .commentsCount(commentRepository.countByBoardId(board.getId()))
                    .likes(likeBoardRepository.countByBoardId(board.getId()))
                    .imAuthor(false)
                    .imLiked(false)
                    .imScrapped(false)
                    .build();
        }
        else {
            return BoardDetailDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .author(toAuthorDto(board.getAuthor()))
                    .category(board.getCategory())
                    .updatedTime(board.getUpdateTime())
                    .writedTime(board.getWriteTime())
                    .photoId(PhotoList)
                    .comments(commentRepository.findByBoardId(board.getId()).stream().map(commentMap::tolistDto).collect(Collectors.toList()))
                    .views(board.getView())
                    .commentsCount(commentRepository.countByBoardId(board.getId()))
                    .likes(likeBoardRepository.countByBoardId(board.getId()))
                    .imAuthor(board.getAuthor().getId().equals(visitor.getId()))
                    .imLiked(likeBoardRepository.findByBoardIdAndUser(board.getId(), visitor).isPresent())
                    .imScrapped(scrapRepository.findByBoardIdAndUser(board.getId(), visitor).isPresent())
                    .build();
        }
    }

    public BoardListDto toListDto(Board board){
        String thumb = "null";
        if (!photoRepository.findAllByBoardId(board.getId()).isEmpty())
            thumb = photoRepository.findAllByBoardId(board.getId()).get(0).getFilePath();
        return BoardListDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .likes(likeBoardRepository.countByBoardId(board.getId()))
                .comments(commentRepository.countByBoardId(board.getId()))
                .authorDto(toAuthorDto(board.getAuthor()))
                .thumbnail(thumb)
                .views(board.getView())
                .build();
    }

    public BoardListMicroDto toListMicroDto(Board board){
        return BoardListMicroDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .category(board.getCategory())
                .likes(likeBoardRepository.countByBoardId(board.getId()))
                .comments(commentRepository.countByBoardId(board.getId()))
                .views(board.getView())
                .build();
    }
    public BoardListMicroMicroDto toListMicroMicroDto(Board board){
        return BoardListMicroMicroDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .build();
    }
}
