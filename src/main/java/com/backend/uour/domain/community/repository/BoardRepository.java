package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
