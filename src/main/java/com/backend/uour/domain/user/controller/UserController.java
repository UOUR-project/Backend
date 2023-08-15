package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto signUpDto) throws Exception{
        userService.signUp(signUpDto);
        return "success";
    }
    @PostMapping("/jwt-test")
    public String jwtTest(){
        return "success";
    }
    @GetMapping("/test/auth")
    @Secured("ROLE_AUTH")
    public @ResponseBody String testAuth(){
        return "auth";
    }
    @GetMapping("/test/unauth")
    @Secured("ROLE_UNAUTH")
    public @ResponseBody String testUnAuth(){
        return "unauth";
    }
    @GetMapping("/test/admin")
    @Secured("ROLE_ADMIN")
    public @ResponseBody String testAdmin(){
        return "admin";
    }
    @GetMapping("/test/authing")
    @Secured("ROLE_AUTHING")
    public @ResponseBody String testAuthing(){
        return "authing";
    }
}
