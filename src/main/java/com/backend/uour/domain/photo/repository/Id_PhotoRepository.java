package com.backend.uour.domain.photo.repository;

import com.backend.uour.domain.photo.entity.Id_Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Id_PhotoRepository extends JpaRepository<Id_Photo,Long> {
}
