package com.dev.museummate.security.oauth;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.security.oauth.userinfo.GoogleOAuth2UserInfo;
import com.dev.museummate.security.oauth.userinfo.NaverOAuth2UserInfo;
import com.dev.museummate.security.oauth.userinfo.OAuth2UserInfo;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@RequiredArgsConstructor
@Slf4j
public class OAuth2Attribute {

    private final String nameAttributeKey;
    private final OAuth2UserInfo oAuth2UserInfo;

    public static OAuth2Attribute of(ProviderType providerType,
                                    String userNameAttributeName, Map<String, Object> attributes) {
        log.info("OAuthAttribute.of() 실행");

        log.info("providerType : {}", providerType);

        if (providerType == ProviderType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuth2Attribute ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                             .nameAttributeKey(userNameAttributeName)
                             .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                             .build();
    }

    public static OAuth2Attribute ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                             .nameAttributeKey(userNameAttributeName)
                             .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                             .build();
    }

    public UserEntity toEntity(ProviderType providerType, OAuth2UserInfo oAuth2UserInfo) {
        log.info("OAuth2Attribute.toEntity() 실행");

        return UserEntity.builder()
                         .providerType(providerType)
                         .providerId(OAuth2Attribute.this.oAuth2UserInfo.getId())
                         .email(UUID.randomUUID() + "@socialUser.com")
                         .userName(OAuth2Attribute.this.oAuth2UserInfo.getNickname())
                         .role(UserRole.ROLE_SOCIAL_USER)
                         .build();
    }

}
