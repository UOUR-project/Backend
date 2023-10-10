package com.backend.uour.domain.community.controller;

import com.backend.uour.domain.community.dto.BoardDetailDto;
import com.backend.uour.domain.community.dto.BoardPostDto;
import com.backend.uour.domain.community.entity.CATEGORY;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.domain.community.service.BoardService;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.network.ResultDTO;
import com.backend.uour.global.network.STATUS;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
// todo: 권한에 맞게 튕겨내줘야함. @Secured 활용.
//@RequestMapping(value = "/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final JwtService jwtService;

    @PostMapping("posting")
//    @Secured("ROLE_AUTHING")
    public ResponseEntity<?> postBoard(@RequestPart BoardPostDto boardPostDto,
                                       @RequestPart(required = false) List<MultipartFile> photos, HttpServletRequest req) throws Exception {
        try {
            String accessToken = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            boardService.save(boardPostDto, photos ,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/{boardId}")
    public ResponseEntity<?> getBoardDetail(@PathVariable("boardId") Long boardId) {
        try {
            BoardDetailDto board = boardService.get(boardId);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, board);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PutMapping("board/{boardId}")
    public ResponseEntity<?> updateBoard(@PathVariable("boardId") Long boardId, @RequestPart BoardPostDto boardPostDto, @RequestPart(required = false) List<MultipartFile> photos , HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            boardService.update(boardId, boardPostDto, photos, accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @DeleteMapping("board/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("boardId") Long boardId, HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            boardService.delete(boardId, accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getBoardListByCategory(@PathVariable("category") CATEGORY category, @RequestParam int page) {
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByCategory(category, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/user")
    public ResponseEntity<?> getBoardListByUser(@RequestParam Long user, @RequestParam int page) {
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByUser(user, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/search")
    public ResponseEntity<?> getBoardListBySearch(@RequestParam String search, @RequestParam int page) {
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getBySearch(search, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/like")
    public ResponseEntity<?> getBoardListByLike(HttpServletRequest req, @RequestParam int page) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByLike(accessToken, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/scrap")
    public ResponseEntity<?> getBoardListByScrap(HttpServletRequest req, @RequestParam int page) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByScrap(accessToken, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/popular")
    public ResponseEntity<?> getBoardListByPopular(@RequestParam int page) {
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByPopular(page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/popular/micro")
    public ResponseEntity<?> getBoardListByPopularMicroMicro(@RequestParam int page) {
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByPopularMicroMicro(page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
// 버튼 동작 코드. -> 토글 구조이기 떄문에 구현에 신경써야함.

    @PostMapping("like")
    public ResponseEntity<?> likeBoard(@RequestParam Long boardId, HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boardService.like(boardId,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PostMapping("scrap")
    public ResponseEntity<?> scrapBoard(@RequestParam Long boardId, HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boardService.scrap(boardId,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    // 사진 업로드 테스트


}

