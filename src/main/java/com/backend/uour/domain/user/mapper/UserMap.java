package com.backend.uour.domain.user.mapper;

import com.backend.uour.domain.community.dto.AuthorDto;
import com.backend.uour.domain.user.dto.MypageDto;
import com.backend.uour.domain.user.dto.StudentDto;
import com.backend.uour.domain.user.dto.StudentIdListDto;
import com.backend.uour.domain.user.entity.StudentId;
import com.backend.uour.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMap {
    public StudentIdListDto toStudentIdListDto(StudentId studentId){
        return StudentIdListDto.builder()
                .id(studentId.getId())
                .user(toStudentDto(studentId.getUser()))
                .Id_photo(studentId.getId_photo().getId())
                .build();
    }

    public MypageDto toMypageDto(User user){
        return MypageDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .school(user.getSchool())
                .major(user.getMajor())
                .build();
    }
    // Author mapper
    public StudentDto toStudentDto(User author){
        return StudentDto.builder()
                .school(author.getSchool())
                .major(author.getMajor())
                .studentId(author.getStudentId())
                .id(author.getId())
                .build();
    }
}
