package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Scrap;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByBoardIdAndUser(Long boardId, User user);
}
