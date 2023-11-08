package com.backend.uour.domain.user.repository;

import com.backend.uour.domain.user.entity.StudentId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentIdRepository extends JpaRepository<StudentId, Long> {
    Slice<StudentId> findSliceBy (Pageable pageable);
}
