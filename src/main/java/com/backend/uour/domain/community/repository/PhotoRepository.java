package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findAllByBoardId (Long boardId);
    void deleteAllByBoardId (Long boardId);
}
