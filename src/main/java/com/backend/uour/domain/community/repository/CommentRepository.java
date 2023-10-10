package com.backend.uour.domain.community.repository;

import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.Comment;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Slice<Comment> findByBoardId(Long boardId,Pageable pageable);
    int countByBoardId(Long boardId);
    List<Comment> findByBoardId(Long boardId);

    List<Comment> findByCommentGroup(Long commentId);

    @Query("SELECT b FROM Comment b JOIN LikeComment lc Where lc.user =: user")
    Slice<Comment> findByLikedUserId(User user, Pageable pageable);

}
