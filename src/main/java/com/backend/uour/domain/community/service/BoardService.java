package com.backend.uour.domain.community.service;
import com.backend.uour.domain.community.dto.BoardDetailDto;
import com.backend.uour.domain.community.dto.BoardListMicroDto;
import com.backend.uour.domain.community.dto.BoardPostDto;
import com.backend.uour.domain.community.dto.BoardListDto;
import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.CATEGORY;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BoardService {
    void save(BoardPostDto board, String accessToken) throws Exception;

    void update(Long boardId, BoardPostDto board, String accessToken) throws Exception;

    void delete(Long boardId, String accessToken) throws Exception;

    BoardDetailDto get(Long boardId) throws Exception;
    Slice<BoardListDto> getByCategory(CATEGORY category, int page); // 카테고리별 정리
    Slice<BoardListDto> getBySearch(String search, int page); // 검색어별 정리


    Slice<BoardListDto> getByCategoryAndSearch(CATEGORY category, String search, int page);

    Slice<BoardListDto> getByUser(Long userid, int page); // 유저별 정리

    // Entity find
    Slice<BoardListDto> getByLike(String accessToken, int page) throws Exception;
    Slice<BoardListDto> getByScrap(String authorization, int page) throws Exception; // 스크랩별 정리
    Slice<BoardListMicroDto> getByPopular(int page); // 인기순 정리

    void like(Long boardId, String authorization) throws Exception;

    void scrap(Long boardId, String authorization) throws Exception;
}
