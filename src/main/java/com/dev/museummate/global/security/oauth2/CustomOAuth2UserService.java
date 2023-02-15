package com.dev.museummate.global.security.oauth2;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 진입");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        // Provider 에서 인증받은 유저 정보
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 어떤 Provider 인지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info(" Provider : [{}]", registrationId);

        // Provider 에서 인증받은 유저 정보의 키
        String userNameAttributeName = userRequest.getClientRegistration()
                                                  .getProviderDetails()
                                                  .getUserInfoEndpoint()
                                                  .getUserNameAttributeName();

        log.info(" userNameAttributeName : [{}]", userNameAttributeName);

        // Provider 에서 인증받은 유저 정보의 값
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Provider 에서 인증받은 유저 정보를 통해 UserProfile 객체 생성
        UserProfile userProfile = OAuth2Attributes.extract(registrationId, attributes);

        Optional<UserEntity> checkUser =
            userRepository.findByEmailAndName(userProfile.getEmail(), userProfile.getName());

        log.info("소셜 로그인 유저 정보 : [{}]", checkUser);

        if (checkUser.isPresent()) {
            log.info("이미 가입된 유저입니다.");

            return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(checkUser.get().getRole().name())),
                attributes,
                userNameAttributeName
            );
        } else {
            log.info("해당 정보의 유저를 찾을 수 없습니다.");
            UserEntity userEntity = UserEntity.builder()
                                              .email(userProfile.getEmail())
                                              .name(userProfile.getName())
                                              .role(UserRole.ROLE_SOCIAL_USER)
                                              .providerId(attributes.get("id").toString())
                                              .providerType(registrationId)
                                              .build();

            userRepository.save(userEntity);

            return new DefaultOAuth2User(
                null,
                attributes,
                userNameAttributeName);
        }
    }
}
