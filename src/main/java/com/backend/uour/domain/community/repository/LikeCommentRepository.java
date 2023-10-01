package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
}
