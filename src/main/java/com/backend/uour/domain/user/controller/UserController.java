package com.backend.uour.domain.user.controller;

import com.backend.uour.domain.user.dto.AdditionUserSignUpDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.entity.BLAME_CATEGORY;
import com.backend.uour.domain.user.service.UserService;
import com.backend.uour.domain.user.dto.UserUpdateDto;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import com.backend.uour.global.network.ResultDTO;
import com.backend.uour.global.network.STATUS;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
//@RequestMapping(value = "/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/sign-up/addition")
    @Secured("ROLE_UNAUTH")
    public ResponseEntity<?> AdditionSignUp(@RequestBody AdditionUserSignUpDto SignUpDto, HttpServletRequest req) throws Exception{
        try {
            String authorization = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            userService.additionSignUp(SignUpDto,authorization);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserSignUpDto signUpDto) throws Exception{
        try{
            userService.signUp(signUpDto);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest req) throws Exception{
        try{
            String authorization = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            userService.deleteUser(authorization);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto, HttpServletRequest req) throws Exception {
        try {
            String authorization = jwtService.extractAccessToken(req).orElseThrow(WrongJwtException::new);
            userService.updateUser(userUpdateDto, authorization);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, null);
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @PostMapping("blame")
    @Secured({"ROLE_AUTH","ROLE_ADMIN"})
    public ResponseEntity<?> blame(@RequestParam Long pointedID, @RequestParam BLAME_CATEGORY blameCategory, HttpServletRequest req){
        try {
            String authorization = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            boolean doBlame = userService.blame(pointedID,authorization,blameCategory);
            if (doBlame) {
                ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, "신고 완료");
                return ResponseEntity.ok(resultDTO);
            }
            else{
                ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, "이미 신고한 대상");
                return ResponseEntity.ok(resultDTO);
            }
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @PostMapping("Promote/Admin")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> promoteAdmin(@RequestParam Long pointedID, HttpServletRequest req){
        try {
            String authorization = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            userService.promoteAdmin(pointedID,authorization);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, "승급 완료");
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
    @PostMapping("Promote/initialSelfPromoteAdmin")
    public ResponseEntity<?> initialSelfPromoteAdmin(HttpServletRequest req){
        try {
            String authorization = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            userService.initialSelfPromoteAdmin(authorization);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, "승급 완료");
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("mypage")
    @Secured({"ROLE_AUTH"})
    public ResponseEntity<?> myPage(HttpServletRequest req){
        try {
            String authorization = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, userService.myPage(authorization));
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("getRole")
    @Secured({"ROLE_AUTH","ROLE_ADMIN","ROLE_UNAUTH","ROLE_AUTHING"})
    public ResponseEntity<?> getRole(HttpServletRequest req){
        try {
            String authorization = jwtService.extractAccessToken(req)
                    .orElseThrow(WrongJwtException::new);
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, userService.getRole(authorization));
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }

    @GetMapping("emailCheck")
    // 로그인 이전에 들어오는 로직이므로 권한 없이 접근 가능
    public ResponseEntity<?> IdCheck(@RequestParam String email){
        try {
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.OK, userService.emailCheck(email));
            return ResponseEntity.ok(resultDTO);
        }
        catch (Exception e){
            ResultDTO<Object> resultDTO = ResultDTO.of(STATUS.BAD_REQUEST, null);
            return ResponseEntity.badRequest().body(resultDTO);
        }
    }
}
