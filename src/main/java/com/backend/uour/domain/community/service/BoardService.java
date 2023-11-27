package com.backend.uour.domain.community.service;
import com.backend.uour.domain.community.dto.*;
import com.backend.uour.domain.community.entity.CATEGORY;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    void save(BoardPostDto board, List<MultipartFile> photos, String accessToken) throws Exception;

    void update(Long boardId, BoardPostDto board,List<MultipartFile> photos, String accessToken) throws Exception;

    void delete(Long boardId, String accessToken) throws Exception;

    BoardDetailDto get(Long boardId, String accessToken) throws Exception;
    Slice<BoardListDto> getByCategory(CATEGORY category, int page); // 카테고리별 정리
    Slice<BoardListDto> getBySearch(String search, int page); // 검색어별 정리


    Slice<BoardListDto> getByCategoryAndSearch(CATEGORY category, String search, int page);

    Slice<BoardListDto> getByUser(Long userid, int page); // 유저별 정리

    // Entity find
    Slice<BoardListDto> getByLike(String accessToken, int page) throws Exception;
    Slice<BoardListDto> getByScrap(String authorization, int page) throws Exception; // 스크랩별 정리
    Slice<BoardListMicroDto> getByPopular(int page); // 인기순 정리

    Slice<BoardListMicroMicroDto> getByPopularMicroMicro(int page);

    void like(Long boardId, String authorization) throws Exception;

    void scrap(Long boardId, String authorization) throws Exception;
}
