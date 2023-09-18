package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.Oauth2UserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.entity.IdImage;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.service.UserService;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.domain.user.service.IdImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final IdImageService idImageService;

    @PostMapping("/oauth2/sign-up")
    public String oauth2SignUp(@RequestBody Oauth2UserSignUpDto oauth2SignUpDto, @RequestHeader("Authorization") String authorization) throws Exception{
        if(authorization.startsWith("Bearer ")){
            authorization = authorization.substring(7);
        }
        else{
            throw new Exception("토큰이 올바르지 않습니다.");
        }
        userService.oauth2SignUp(oauth2SignUpDto,authorization);
        return "success";
    }
    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto signUpDto) throws Exception{
        userService.signUp(signUpDto);
        return "success";
    }

    @PostMapping("/jwt-test")
    public String jwtTest(){
        return "success";
    }

    @PostMapping(value = "/id-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long saveIdImage(HttpServletRequest req, @RequestParam("image") MultipartFile image, IdImage idImage) throws IOException {
        System.out.println(image);
        System.out.println(idImage);
        return idImageService.saveIdImage(image,idImage);
    }
}
