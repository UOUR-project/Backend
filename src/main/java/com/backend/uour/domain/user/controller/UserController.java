package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.Oauth2UserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.service.UserService;
import com.backend.uour.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

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
