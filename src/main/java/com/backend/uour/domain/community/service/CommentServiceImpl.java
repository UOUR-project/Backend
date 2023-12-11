package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.dto.CommentListDto;
import com.backend.uour.domain.community.dto.CommentPostDto;
import com.backend.uour.domain.community.entity.Comment;
import com.backend.uour.domain.community.entity.LikeBoard;
import com.backend.uour.domain.community.entity.LikeComment;
import com.backend.uour.domain.community.mapper.CommentMap;
import com.backend.uour.domain.community.repository.BoardRepository;
import com.backend.uour.domain.community.repository.CommentRepository;
import com.backend.uour.domain.community.repository.LikeBoardRepository;
import com.backend.uour.domain.community.repository.LikeCommentRepository;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.exception.NoCommentException;
import com.backend.uour.global.exception.NoUserException;
import com.backend.uour.global.exception.NotSameAuthorException;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService{
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CommentMap commentMap;
    private final CommentRepository commentRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final BoardRepository boardRepository;
    @Override
    public void save(Long boardId, Long CommentGroup, CommentPostDto commentDto, String accessToken) throws Exception{
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Comment newcomment = commentMap.toEntity(commentDto,CommentGroup,author,boardId);
        if(newcomment.getCommentGroup() != -1){
            if(commentRepository.findById(newcomment.getCommentGroup()).orElseThrow(NoCommentException::new).getCommentGroup() == -1) {
                //대댓글 저장
                commentRepository.save(newcomment);
                boardRepository.findById(boardId).get().addComment(newcomment);
            }

            else
                throw new NoCommentException();
        }
        else{
            //댓글 저장
            commentRepository.save(newcomment);
        }
    }

    @Override
    public void delete(Long boardId, Long commentId, String accessToken) throws Exception{
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoCommentException::new);
        if(comment.getAuthor() == author){
            if(comment.getCommentGroup() != -1){
                // 대댓글 삭제
                commentRepository.delete(comment);
            }
            else{
                // 댓글 삭제
                List<Comment> temp = commentRepository.findByCommentGroup(comment.getId());
                commentRepository.deleteAll(temp);
                commentRepository.delete(comment);
            }
        }
        else {
            throw new NotSameAuthorException();
        }
    }

    @Override
    public Slice<CommentListDto> getByBoardId(Long boardId, int page, String accessToken) throws Exception{
        Slice<Comment> comments = commentRepository.findByBoardId(boardId, PageRequest.of(page,7));
        User visitor = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        return comments.map(comment -> commentMap.tolistDto(comment, visitor));
    }

    @Override
    public boolean like(Long boardId, Long commentId, String accessToken) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Optional<LikeComment> temp = likeCommentRepository.findByCommentIdAndUser(commentId, user);
        if(temp.isEmpty()){
            LikeComment likeComment = LikeComment.builder()
                    .comment(commentRepository.findById(commentId).get())
                    .user(user)
                    .build();
            likeCommentRepository.save(likeComment);
            return true; // 만들어졌다.
        }
        else{
            likeCommentRepository.delete(temp.get());
            return false; // 삭제됐다.
        }
    }
}
