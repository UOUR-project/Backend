package com.backend.uour.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ROLE {
    AUTH("ROLE_AUTH"), // 인증된 사용자
    ADMIN("ROLE_ADMIN"), // 관리자
    UNAUTH("ROLE_UNAUTH"), // 인증되지 않은 사용자
    AUTHING("ROLE_AUTHING"); // 인증중인 사용자

    private final String key;
}
