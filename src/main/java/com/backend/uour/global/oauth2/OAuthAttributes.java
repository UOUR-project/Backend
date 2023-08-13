package com.backend.uour.global.oauth2;

import com.backend.uour.domain.user.entity.ROLE;
import com.backend.uour.domain.user.entity.SOCIAL;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.global.oauth2.info.GoogleOAuth2UserInfo;
import com.backend.uour.global.oauth2.info.KakaoOAuth2UserInfo;
import com.backend.uour.global.oauth2.info.NaverOAuth2UserInfo;
import com.backend.uour.global.oauth2.info.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private String nameAttributeKey; // OAuth2 로그인 진행시 키가 되는 필드값 (PK)
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    // 소셜 타입에 맞게 하나하나 매핑...
    public static OAuthAttributes of(SOCIAL socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SOCIAL.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (socialType == SOCIAL.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }
    // of 메소드를 통해 OAuthAttributes를 생성하는 시점은 처음 가입할 때
    // 빌드를 해주고 role -> UNAUTH 로.. ->  추후에 학교랑 이런거 넣으면 바꿔줄거임.
    // 근데 닉네임 쓰는 의미가 있을까?

    public User toEntity(SOCIAL social, OAuth2UserInfo userInfo){
        return User.builder()
                .email(UUID.randomUUID() + "@socialUser.com")
                .socialId(userInfo.getId())
                .name(userInfo.getNickname())
                .socialType(social)
                .role(ROLE.UNAUTH)
                .build();
    }
}
