package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.Comment;
import com.backend.uour.domain.community.entity.LikeBoard;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {
    // 좋아요 객체 카운트
    int countByBoardId(Long boardId);
    Optional<LikeBoard> findByBoardIdAndUser(Long boardId, User user);
}
