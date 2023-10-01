package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.LikeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {
}
