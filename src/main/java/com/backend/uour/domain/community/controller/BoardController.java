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
//@RequestMapping(value = "/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final JwtService jwtService;

    @PostMapping("posting")
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
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
    public ResponseEntity<?> getBoardDetail(@PathVariable("boardId") Long boardId, HttpServletRequest req) {
        try {
            BoardDetailDto board;
            if(jwtService.extractAccessToken(req).isEmpty())
                board = boardService.get(boardId, null);
            else {
                String accessToken = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
                board = boardService.get(boardId, accessToken);
            }
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, board);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PutMapping("board/{boardId}")
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
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
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
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
    // 일반 사용자도 보기는 가능.
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
    // 일반 사용자도 보기는 가능.
    public ResponseEntity<?> getBoardListByUser(HttpServletRequest req, @RequestParam int page) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, boardService.getByUser(accessToken, page));
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("board/search")
    // 일반 사용자도 보기는 가능.
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
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
    // 일반 사용자 불가능
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
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
    // 일반사용자 불가능
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
    // 일반 사용자도 보기는 가능.
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
    // 일반 사용자도 보기는 가능.
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
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
    public ResponseEntity<?> likeBoard(@RequestParam Long boardId, HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boolean isLike = boardService.like(boardId,accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK,isLike);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PostMapping("scrap")
    @Secured({"ROLE_AUTH", "ROLE_ADMIN"})
    public ResponseEntity<?> scrapBoard(@RequestParam Long boardId, HttpServletRequest req) {
        try {
            String accessToken = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boolean isScrap = boardService.scrap(boardId, accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, isScrap);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
}

