package com.backend.uour.global.oauth2.service;

import com.backend.uour.domain.user.entity.SOCIAL;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.oauth2.CustomOAuth2User;
import com.backend.uour.global.oauth2.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

//OAuth2 로그인 이후 가져온 사용자의 정보들을 기반으로 가입 및 정보수정, 세션 저장 등의 기능을 지원
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth 로그인 진입");
        // loadUser는 소셜로그인 Api의 정보제공 URI로 요청 올려서 정보 얻은뒤, 이를 담은 DefaultOAuth2User를 반환
        // OAuth2User는 OAuth2 에서 가져온 유저 정보를 가지고있다.

        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest); //디폴트를 빌려 일단 가져옴

        // registrationId 추출 -> SOCIAL
        // userNameAttributeName 추출 -> PK -> 이후 그냥 nameAttributeKey로 사용

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SOCIAL socialType = getSOCIAL(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 진행시 키가 되는 필드값 (PK)
        Map<String,Object> attributes = oAuth2User.getAttributes(); // OAuth2UserService를 통해 가져온 유저 정보 (실질적인 정보)

        // SOCIAL에 따라 OAuthAttributes 다르게 만들어주어야함
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        User createdUser = getUser(oAuthAttributes,socialType);

        // DefauleOAuth2User 구현한 CustomOAuth2User를 반환 (슈퍼 3개, 나머지 내가 넣을것들)
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attributes,
                oAuthAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                createdUser.getRole(),
                createdUser.getSchool(),
                createdUser.getMajor(),
                createdUser.getStudentId(),
                createdUser.getNickname()
        );
    }

    // 이제 로그인이면 로그인, 회원가입이면 회원가입을 해야함.
    private SOCIAL getSOCIAL(String registrationId){
        if(registrationId.equals(NAVER)){
            return SOCIAL.NAVER;
        }
        else if(registrationId.equals(KAKAO)){
            return SOCIAL.KAKAO;
        }
        else{
            return SOCIAL.GOOGLE;
        }
    }

    private User getUser(OAuthAttributes oAuthAttributes, SOCIAL social){
        User user = userRepository.findBySocialTypeAndSocialId(social,oAuthAttributes.getOAuth2UserInfo().getId())
                .orElse(null);

        // 회원가입이면 회원가입을 해야함.
        if(user == null){
            return saveUser(oAuthAttributes,social);
        }
        // 로그인이면 로그인을 해야함.
        else{
            return user;
        }
    }

    private User saveUser(OAuthAttributes oAuthAttributes, SOCIAL social){
        User user = oAuthAttributes.toEntity(social,oAuthAttributes.getOAuth2UserInfo());
        return userRepository.save(user);
    }
}
