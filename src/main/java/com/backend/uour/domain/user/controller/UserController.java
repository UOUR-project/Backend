package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signUp(@RequestBody UserSignUpDto signUpDto) throws Exception{
        userService.signUp(signUpDto);
        return "success";
    }
    @PostMapping("/jwt-test")
    public String jwtTest(){
        return "success";

    }
}
