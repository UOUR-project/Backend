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
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final JwtService jwtService;
    private final CommentService commentService;

    @PostMapping("comment/{boardId}")
    public ResponseEntity<?> postComment(@PathVariable Long boardId, @RequestBody CommentPostDto commentDto , HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            commentService.save(boardId,commentDto,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok().body(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @GetMapping("comment/{boardId}")
    public ResponseEntity<?> getCommentList(@PathVariable Long boardId,@RequestParam int page){
        try{
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, commentService.getByBoardId(boardId, page));
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @DeleteMapping("comment/{boardId}")
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
    public ResponseEntity<?> likeComment(@PathVariable Long boardId,@RequestParam Long commentId, HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            commentService.like(boardId,commentId,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
}
