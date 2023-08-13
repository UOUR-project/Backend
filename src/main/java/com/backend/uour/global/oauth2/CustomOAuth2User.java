package com.backend.uour.global.oauth2;

import com.backend.uour.domain.user.entity.ROLE;
import com.backend.uour.domain.user.entity.SCHOOL;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

// 리소스 서버에서 제공하지않는 정보를 받기위해 (소셜로그인 추가정보 받아오기)
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    // 받아야할 추가정보.
    private String email;
    private ROLE role;
    private SCHOOL school; // -> 추가정보
    private String major; // -> 추가정보
    private String studentId; // -> 추가정보
    private String nickname; // -> 나중에 앱에서 사용할 닉네임도 추가로 받아주기.



    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     *                         {@link #getAttributes()}
     */
    // 나중에 ROLE_UNAUTH로 되어있으면 쟤네들 추가로 받아야함.
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey,
                            String email, ROLE role, SCHOOL school, String major, String studentId, String nickname) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
        this.school = school;
        this.major = major;
        this.studentId = studentId;
        this.nickname = nickname;
    }
}
