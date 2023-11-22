package com.backend.uour.domain.user.service;

import com.backend.uour.domain.user.dto.AdditionUserSignUpDto;
import com.backend.uour.domain.user.dto.MypageDto;
import com.backend.uour.domain.user.dto.UserSignUpDto;
import com.backend.uour.domain.user.dto.UserUpdateDto;
import com.backend.uour.domain.user.entity.BLAME_CATEGORY;
import com.backend.uour.domain.user.entity.ROLE;

public interface UserService {
    void signUp(UserSignUpDto userSignUpDto) throws Exception;
    void additionSignUp(AdditionUserSignUpDto userSignupDto, String temp) throws Exception;
    void deleteUser(String temp) throws Exception;
    void updateUser(UserUpdateDto updateDto, String temp) throws Exception;
    boolean blame(Long pointed_outId, String authorization, BLAME_CATEGORY blameCategory) throws Exception;
    void promoteAdmin(Long pointedId, String authorization) throws Exception;

    MypageDto myPage(String authorization) throws Exception;
    ROLE getRole(String authorization) throws Exception;
    boolean emailCheck(String email) throws Exception;
    void initialSelfPromoteAdmin(String authorization) throws Exception;
}
