package com.dev.museummate.service;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.security.oauth.OAuth2Attribute;
import com.dev.museummate.security.oauth.ProviderType;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.utils.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisDao redisDao;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${cookie.maxage}")
    private Long maxAge;


    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_FOUND, String.format("%s님은 존재하지 않습니다.",email)));
    }

    public UserEntity findUserByProviderTypeAndProviderId(ProviderType providerType, String providerId) {
        return userRepository.findByProviderTypeAndProviderId(providerType, providerId)
                             .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                                 String.format("%s 는 존재하지 않습니다.",providerType)));
    }

    public String join(UserJoinRequest userJoinRequest) {
        log.info("회원가입 요청 : {}",userJoinRequest);

        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user ->{
                    throw new AppException(ErrorCode.DUPLICATE_USERNAME,String.format("%s는 중복 된 닉네임입니다.",userJoinRequest.getUserName()));
                });

        userRepository.findByEmail(userJoinRequest.getEmail())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_EMAIL,String.format("%s는 중복 된 이메일입니다.",userJoinRequest.getEmail()));
                });

        UserEntity user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));
        UserEntity savedUser = userRepository.save(user);

        UserDto userDto = UserDto.toDto(savedUser);

        return userDto.getEmail();

    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        UserEntity findUser = findUserByEmail(userLoginRequest.getEmail());

        if(!encoder.matches(userLoginRequest.getPassword(), findUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,String.format("잘못된 비밀번호 입니다."));
        }
        if (findUser.getAuth().equals(Boolean.FALSE)) {
            throw new AppException(ErrorCode.INVALID_MAIL, String.format("인증 되지 않은 이메일 입니다."));
        }


        String accessToken = jwtProvider.createAccessToken(userLoginRequest.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(userLoginRequest.getEmail());

        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken, maxAge,TimeUnit.SECONDS);

        return new UserLoginResponse(accessToken,refreshToken);
    }

    public UserLoginResponse reissue(UserReissueRequest userReissueRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        if (jwtProvider.isExpired(userReissueRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN,"만료된 토큰입니다.");
        }

        String refreshToken = (String)redisDao.getValues("RT:" + email);

        if(ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REQUEST,"잘못된 요청입니다");
        }
        if(!refreshToken.equals(userReissueRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN,"잘못된 토큰입니다.");
        }

        // 4. 새로운 토큰 생성
        String accessToken = jwtProvider.createAccessToken(findUser.getEmail());

        // 5. RefreshToken Redis 업데이트
        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken);

        return new UserLoginResponse(accessToken,refreshToken);

    }

    public String logout(UserReissueRequest userLogoutRequest, String email) {

        String str = (String)redisDao.getValues("RT:" + email);

        log.info("str = {}", str);

        if(!StringUtils.isEmpty(redisDao.getValues("RT:" + email))) {
            redisDao.deleteValues("RT:" + email);
        }

        int expiration = jwtProvider.getExpiration(userLogoutRequest.getAccessToken()).intValue()/1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(userLogoutRequest.getAccessToken(), "logout",expiration,TimeUnit.SECONDS);

        return "로그아웃 되었습니다.";

    }

    public String userNameCheck(UserCheckRequest userCheckRequest) {
        userRepository.findByUserName(userCheckRequest.getUserName())
                .ifPresent(user ->{
                    throw new AppException(ErrorCode.DUPLICATE_USERNAME,String.format("%s는 중복 된 닉네임입니다.",userCheckRequest.getUserName()));
                });
        return "사용 가능한 닉네임 입니다.";
    }

    @Transactional
    public String modifyUser(UserModifyRequest userModifyRequest, String email) {

        userRepository.findByUserName(userModifyRequest.getUserName())
                .ifPresent(user ->{
                    throw new AppException(ErrorCode.DUPLICATE_USERNAME,String.format("%s는 중복 된 닉네임입니다.",userModifyRequest.getUserName()));
                });

        UserEntity findUser = findUserByEmail(email);

        findUser.updateInfo(userModifyRequest);

        userRepository.save(findUser);

        return "수정이 완료 되었습니다.";
    }

    public String deleteUser(String email) {

        UserEntity findUser = findUserByEmail(email);

        userRepository.delete(findUser);

        return "탈퇴가 완료 되었습니다.";
    }

    @Transactional
    public String auth(String authNum, String email) {
        UserEntity findUser = findUserByEmail(email);

        if (findUser.getAuthNum().equals(authNum)) {
            findUser.updateAuth();
            userRepository.save(findUser);
            return "인증이 완료 되었습니다.";
        }
        return "인증에 실패 했습니다.";
    }

    public UserEntity saveOAuth2User(OAuth2Attribute attributes, ProviderType providerType) {
        UserEntity createdUser = attributes.toEntity(providerType, attributes.getOAuth2UserInfo());
        return userRepository.save(createdUser);
    }
}
