package com.backend.uour.domain.user.service;

import com.backend.uour.domain.photo.entity.Id_Photo;
import com.backend.uour.domain.photo.repository.Id_PhotoRepository;
import com.backend.uour.domain.photo.service.PhotoHandler;
import com.backend.uour.domain.photo.service.PhotoService;
import com.backend.uour.domain.user.dto.StudentIdListDto;
import com.backend.uour.domain.user.entity.StudentId;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.mapper.UserMap;
import com.backend.uour.domain.user.repository.StudentIdRepository;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.exception.NoUserException;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StudentIdServiceImpl implements StudentIdService{
    private final JwtService jwtService;
    private final PhotoHandler photoHandler;
    private final PhotoService photoService;
    private final UserRepository userRepository;
    private final StudentIdRepository studentIdRepository;
    private final Id_PhotoRepository Id_photoRepository;
    private final UserMap userMap;
    @Override
    public void apply(MultipartFile photo, String accessToken) throws Exception {
        User user = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        StudentId studentId = new StudentId(user);
        Id_Photo savedPhoto = photoHandler.parseS3StudentId(photo, studentId);
        studentId.setId_Photo(savedPhoto);
        try {
            Id_photoRepository.save(savedPhoto);
            studentIdRepository.save(studentId);
        }
        catch (Exception e){
            photoService.deleteId_Photo(studentId.getId_photo());
        }
    }

    @Override
    public Slice<StudentIdListDto> getStudentIdList(int page) throws Exception{
        Page<StudentId> studentIdList = studentIdRepository.findAll(PageRequest.of(page,10));
        return studentIdList.map(userMap::toStudentIdListDto);
    }

    @Override
    public void approve(Long studentIdId) throws Exception {
        // 승인하면 삭제시키기.
        StudentId studentId = studentIdRepository.findById(studentIdId).orElseThrow(NoUserException::new);
        User student = studentId.getUser();    // 승인할 학생
        // 승인한다면 -> 기존에 있던 studentId 삭제, 사진도 동시에 삭제, 이 student 의 권한 변경.
        studentIdRepository.delete(studentId);
        photoService.deleteId_Photo(studentId.getId_photo());
        student.authtorizeAuth();
    }
}
