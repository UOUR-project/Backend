package com.backend.uour.domain.community.controller;

import com.backend.uour.domain.community.dto.CommentPostDto;
import com.backend.uour.domain.community.service.CommentService;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.network.ResultDTO;
import com.backend.uour.global.network.STATUS;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final JwtService jwtService;
    private final CommentService commentService;

    @PostMapping("comment/{boardId}")
    @Secured({"ROLE_AUTH","ROLE_ADMIN"})
    //commentGroup -> 만약 대댓글이면 댓글의 id, 아니면 없다.
    public ResponseEntity<?> postComment(@PathVariable Long boardId,@RequestParam(required = false) Long commentGroup, @RequestBody CommentPostDto commentDto , HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            if (commentGroup == null)
                commentGroup = -1L;
            commentService.save(boardId,commentGroup,commentDto,accessToken); // commentGroup -> 만약 대댓글이면 댓글의 id, 아니면 -1
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok().body(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @GetMapping("comment/{boardId}")
    @Secured({"ROLE_AUTH","ROLE_ADMIN"})
    public ResponseEntity<?> getCommentList(@PathVariable Long boardId,@RequestParam int page, HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, commentService.getByBoardId(boardId, page, accessToken));
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @DeleteMapping("comment/{boardId}")
    @Secured({"ROLE_AUTH","ROLE_ADMIN"})
    //commentId -> 삭제할 댓글의 id
    public ResponseEntity<?> deleteComment(@PathVariable Long boardId,@RequestParam Long commentId, HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            commentService.delete(boardId, commentId, accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok().body(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @PostMapping("comment/{boardId}/like")
    @Secured({"ROLE_AUTH","ROLE_ADMIN"})
    //commentId -> 좋아요를 누를 댓글의 id
    public ResponseEntity<?> likeComment(@PathVariable Long boardId,@RequestParam Long commentId, HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boolean isLike = commentService.like(boardId,commentId,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,isLike);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
}
