package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.dto.CommentListDto;
import com.backend.uour.domain.community.dto.CommentPostDto;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


public interface CommentService {
    void save(Long boardId,CommentPostDto commentDto, String accessToken)throws Exception;
    void delete(Long boardId,Long commentId, String accessToken)throws Exception;
    Slice<CommentListDto> getByBoardId(Long boardId, int page);
    void like(Long boardId, Long commentId, String accessToken)throws Exception;

}
