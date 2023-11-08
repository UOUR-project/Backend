package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.StudentIdListDto;
import com.backend.uour.domain.user.service.StudentIdService;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.network.ResultDTO;
import com.backend.uour.global.network.STATUS;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentIdController {

    private final JwtService jwtService;
    private final StudentIdService studentIdService;

    // 인증 신청 리스트
    @GetMapping("studentid")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> GetStudentIdList(@RequestParam int page){
        try{
            System.out.println(page);
            Slice<StudentIdListDto> studentIdList = studentIdService.getStudentIdList(page);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, studentIdList);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception E){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    // 인증 신청하기
    @PostMapping("/studentid/apply")
    @Secured("ROLE_AUTHING")
    public ResponseEntity<?> ApplyStudentId(@RequestPart MultipartFile Id_photo, HttpServletRequest req){
        try{
            String accessToken = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            studentIdService.apply(Id_photo, accessToken);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception E){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }

    }


    // 인증 신청 승인
    @PostMapping("/studentid/approve")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> ApproveStudentId(@RequestParam Long studentId){
        try{
            studentIdService.approve(studentId);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception E){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

}
