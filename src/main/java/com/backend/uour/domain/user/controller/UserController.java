package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.Oauth2UserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.service.UserService;
import com.backend.uour.domain.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

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

    @DeleteMapping("/user")
    public String deleteUser(@RequestHeader("Authorization") String authorization) throws Exception{
        if(authorization.startsWith("Bearer ")){
            authorization = authorization.substring(7);
        }
        else{
            throw new Exception("토큰이 올바르지 않습니다.");
        }
        userService.deleteUser(authorization);
        return "success";
    }

    @PutMapping("/user")
    public String updateUser(@RequestBody UserUpdateDto userUpdateDto, @RequestHeader("Authorization") String authorization) throws Exception{
        if(authorization.startsWith("Bearer ")){
            authorization = authorization.substring(7);
        }
        else{
            throw new Exception("토큰이 올바르지 않습니다.");
        }
        userService.updateUser(userUpdateDto,authorization);
        return "success";
    }
}
