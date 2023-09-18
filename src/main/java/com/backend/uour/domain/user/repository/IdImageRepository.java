package com.backend.uour.domain.user.repository;

import com.backend.uour.domain.user.entity.IdImage;
import com.backend.uour.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdImageRepository extends JpaRepository<IdImage, Long> {
    Optional<IdImage> findByUser(User user);
}
