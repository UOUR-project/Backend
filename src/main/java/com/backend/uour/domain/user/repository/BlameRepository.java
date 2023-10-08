package com.backend.uour.domain.user.repository;

import com.backend.uour.domain.user.entity.Blame;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlameRepository extends JpaRepository<Blame, Long> {
    Optional<Blame> findByUserIdAndPointedId(Long userid, Long pointedId);
}
