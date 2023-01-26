package com.dev.museummate.service;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.Transient;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisDao redisDao;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long accessExpireTimeMs = 1000 * 60 * 5;
    private static final long refreshExpireTimeMs = 1000 * 60 * 30;


    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_FOUND, String.format("%s님은 존재하지 않습니다.",email)));
    }

    public UserDto join(UserJoinRequest userJoinRequest) {

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

        return userDto;

    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        UserEntity findUser = findUserByEmail(userLoginRequest.getEmail());

        if(!encoder.matches(userLoginRequest.getPassword(), findUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,String.format("잘못된 비밀번호 입니다."));
        }

        String accessToken = JwtUtils.createAccessToken(userLoginRequest.getEmail(), secretKey, accessExpireTimeMs);
        String refreshToken = JwtUtils.createRefreshToken(userLoginRequest.getEmail(), secretKey, refreshExpireTimeMs);

        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken,60*30,TimeUnit.SECONDS);

        return new UserLoginResponse(accessToken,refreshToken);
    }

    public UserLoginResponse reissue(UserReissueRequest userReissueRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        if (JwtUtils.isExpired(userReissueRequest.getRefreshToken(), secretKey)) {
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
        String accessToken = JwtUtils.createAccessToken(findUser.getEmail(), secretKey ,accessExpireTimeMs);

        // 5. RefreshToken Redis 업데이트
        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken);

        return new UserLoginResponse(accessToken,refreshToken);

    }

    public String logout(UserReissueRequest userLogoutRequest, String email) {

        String str = (String)redisDao.getValues("RT:" + email);

        System.out.println("str = " + str);

        if(!StringUtils.isEmpty(redisDao.getValues("RT:" + email))) {
            redisDao.deleteValues("RT:" + email);
        }

        int expiration = JwtUtils.getExpiration(userLogoutRequest.getAccessToken(), secretKey).intValue()/1000;

        System.out.println("expiration = " + expiration);

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
}
