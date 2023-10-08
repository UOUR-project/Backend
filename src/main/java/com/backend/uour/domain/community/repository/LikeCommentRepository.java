package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Comment;
import com.backend.uour.domain.community.entity.LikeComment;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
    // 좋아요 객체 카운트
    int countByCommentId(Long commentId);
    Optional<LikeComment> findByCommentIdAndUser(Long Comment, User user);
}
