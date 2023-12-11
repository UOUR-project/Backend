package com.backend.uour.domain.community.mapper;


import com.backend.uour.domain.community.dto.AuthorDto;
import com.backend.uour.domain.community.dto.CommentListDto;
import com.backend.uour.domain.community.dto.CommentPostDto;
import com.backend.uour.domain.community.entity.Comment;
import com.backend.uour.domain.community.repository.BoardRepository;
import com.backend.uour.domain.community.repository.LikeCommentRepository;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.exception.NoPostingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMap {

    private final BoardRepository boardRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final UserRepository userRepository;

    public AuthorDto toAuthorDto(User author){
        return AuthorDto.builder()
                .nickname(author.getNickname())
                .school(author.getSchool())
                .id(author.getId())
                .build();
    }

    public Comment toEntity(CommentPostDto commentPostDto, Long CommentGroup,User author,Long boardId) throws Exception{
        return Comment.builder()
                .author(author)
                .commentGroup(CommentGroup)
                .board(boardRepository.findById(boardId)
                        .orElseThrow(NoPostingException::new))
                .content(commentPostDto.getContent())
                .build();
    }
    public CommentListDto tolistDto(Comment comment, User user) {
        if (user == null) {
            return CommentListDto.builder()
                    .id(comment.getId())
                    .author(toAuthorDto(comment.getAuthor()))
                    .content(comment.getContent())
                    .commentGroup(comment.getCommentGroup())
                    .WriteTime(comment.getWriteTime())
                    .updateTime(comment.getUpdateTime())
                    .likes(likeCommentRepository.countByCommentId(comment.getId()))
                    .isMine(false)
                    .build();
        } else {
            return CommentListDto.builder()
                    .id(comment.getId())
                    .author(toAuthorDto(comment.getAuthor()))
                    .content(comment.getContent())
                    .commentGroup(comment.getCommentGroup())
                    .WriteTime(comment.getWriteTime())
                    .updateTime(comment.getUpdateTime())
                    .likes(likeCommentRepository.countByCommentId(comment.getId()))
                    .isMine(comment.getAuthor().getId().equals(user.getId()))
                    .build();
        }
    }
}
