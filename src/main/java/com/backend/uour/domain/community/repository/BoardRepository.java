package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.CATEGORY;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Slice<Board> findByCategory(CATEGORY category, Pageable pageable);
    Slice<Board> findByAuthor(User user,Pageable pageable);
    Slice<Board> findByTitleContaining(String search,Pageable pageable);
    Slice<Board> findByCategoryAndTitleContaining(CATEGORY category,String search,Pageable pageable);
    // todo: 본문 검색, 제목 + 본문 검색!

    @Query("SELECT b FROM Board b JOIN LikeBoard lb ON b = lb.board WHERE lb.user = :user")
    Slice<Board> findByLikedUser(User user, Pageable pageable);


    @Query("SELECT b FROM Board b JOIN Scrap s ON b = s.board WHERE s.user = :user")
    Slice<Board> findByScrapedUser(User user, Pageable pageable);

    Slice<Board> findByWriteTimeBetween (LocalDateTime start, LocalDateTime end, Pageable pageable);
}
