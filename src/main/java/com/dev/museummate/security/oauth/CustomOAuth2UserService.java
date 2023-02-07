package com.dev.museummate.security.oauth;

import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.service.UserService;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


//    private final HttpSession httpSession;
    private final UserService userService;
    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2User Login Request - CustomOAuth2UserService.loadUser 실행");

        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // ProviderType
        String registrationId = userRequest.getClientRegistration()
                                           .getRegistrationId();
        log.info("Provider : {}", registrationId);

        ProviderType providerType = getProviderType(registrationId);

        // PK 값
        String userNameAttributeName = userRequest.getClientRegistration()
                                                  .getProviderDetails()
                                                  .getUserInfoEndpoint()
                                                  .getUserNameAttributeName();
        log.info("userNameAttributeName : {}", userNameAttributeName);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuthAttribute 객체 생성(ProviderType, PK, OAuth2UserInfo)
        OAuth2Attribute extractedAttributes = OAuth2Attribute.of(providerType, userNameAttributeName, attributes);

        // UserEntity 객체 생성 및 저장
        UserEntity createdUser = getAndSaveUser(extractedAttributes, providerType);

//        if (createdUser != null) {
//            httpSession.setAttribute("id", createdUser.getId());
//
//        }

        // DefaultOAuth2User 객체를 구현한 CustomOAuth2User 객체 생성 및 반환
        return new CustomOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getRole())),
            attributes,
            extractedAttributes.getNameAttributeKey(),
            createdUser.getEmail(),
            createdUser.getRole()
        );

    }

    private ProviderType getProviderType(String registrationId) {
        log.info("OAuth2User Login Request - CustomOAuth2UserService.getProviderType 실행");

        if (registrationId.equals(KAKAO)) {
            return ProviderType.KAKAO;
        } else if (registrationId.equals(NAVER)) {
            return ProviderType.NAVER;
        } else {
            return ProviderType.GOOGLE;
        }

    }

    private UserEntity getAndSaveUser(OAuth2Attribute attributes, ProviderType providerType) {
        log.info("OAuth2User Login Request - CustomOAuth2UserService.getUser 실행");

        UserEntity findUser =
            userService.findUserByProviderTypeAndProviderId(providerType,
                                                            attributes.getOAuth2UserInfo()
                                                                      .getId());

        if (findUser == null) {
            return saveUser(attributes, providerType);
        }

        return findUser;
    }

    private UserEntity saveUser(OAuth2Attribute attributes, ProviderType providerType) {
        log.info("OAuth2User Login Request - CustomOAuth2UserService.saveUser 실행");

        return userService.saveOAuth2User(attributes, providerType);
    }

}
