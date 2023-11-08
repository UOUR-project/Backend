package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.dto.StudentIdListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface StudentIdService {
    void apply(MultipartFile photo, String accessToken) throws Exception;
    Slice<StudentIdListDto> getStudentIdList(int page) throws Exception;
    void approve(Long studentId) throws Exception;

}
