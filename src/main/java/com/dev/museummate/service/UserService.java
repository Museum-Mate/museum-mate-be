package com.dev.museummate.service;

import com.dev.museummate.domain.dto.user.UserCheckRequest;
import com.dev.museummate.domain.dto.user.UserDto;
import com.dev.museummate.domain.dto.user.UserJoinRequest;
import com.dev.museummate.domain.dto.user.UserLoginRequest;
import com.dev.museummate.domain.dto.user.UserLoginResponse;
import com.dev.museummate.domain.dto.user.UserModifyRequest;
import com.dev.museummate.domain.dto.user.UserTokenRequest;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.global.redis.RedisDao;
import com.dev.museummate.global.utils.JwtUtils;
import com.dev.museummate.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisDao redisDao;
    private final JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    public String secretKey;
    @Value("${jwt.access.expiration}")
    public Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    public Long refreshTokenExpiration;
    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                                                                 new AppException(ErrorCode.EMAIL_NOT_FOUND,
                                                                                  String.format("%s님은 존재하지 않습니다.", email)));
    }

    @Transactional
    public String join(UserJoinRequest userJoinRequest) {
        log.info("회원가입 요청 : {}", userJoinRequest);

        userRepository.findByUserName(userJoinRequest.getUserName())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                 String.format("%s는 중복 된 닉네임입니다.", userJoinRequest.getUserName()));
                      });

        userRepository.findByEmail(userJoinRequest.getEmail())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_EMAIL, String.format("%s는 중복 된 이메일입니다.", userJoinRequest.getEmail()));
                      });

        UserEntity user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));
        UserEntity savedUser = userRepository.save(user);

        UserDto userDto = UserDto.toDto(savedUser);
        log.info("회원가입 완료!");
        log.info("email: {}, name: {}, userName: {}", savedUser.getEmail(), savedUser.getName(), savedUser.getUserName());

        return userDto.getEmail();
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        UserEntity findUser = findUserByEmail(userLoginRequest.getEmail());

        if (!encoder.matches(userLoginRequest.getPassword(), findUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "잘못된 비밀번호 입니다.");
        }
        if (findUser.getAuth().equals(Boolean.FALSE)) {
            throw new AppException(ErrorCode.INVALID_MAIL, "인증 되지 않은 이메일 입니다.");
        }

        String accessToken = jwtUtils.createAccessToken(userLoginRequest.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(userLoginRequest.getEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new UserLoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public UserLoginResponse reissue(UserTokenRequest userTokenRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        if (jwtUtils.isExpired(userTokenRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "만료된 토큰입니다.");
        }

        String refreshToken = redisDao.getValues("RT:" + email);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "잘못된 요청입니다");
        }
        if (!refreshToken.equals(userTokenRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        }

        String newAccessToken = jwtUtils.createAccessToken(findUser.getEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(findUser.getEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findUser.getEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new UserLoginResponse(newAccessToken, refreshToken);

    }

    public String logout(UserTokenRequest userTokenRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        String accessToken = userTokenRequest.getAccessToken();

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "만료된 토큰입니다.");
        }

        if (!jwtUtils.isValid(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        }

        // Token 삭제
        redisDao.deleteValues("RT:" + findUser.getEmail());

        int expiration = jwtUtils.getExpiration(userTokenRequest.getAccessToken()).intValue() / 1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(userTokenRequest.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return "로그아웃 되었습니다.";
    }

    public String userNameCheck(UserCheckRequest userCheckRequest) {
        userRepository.findByUserName(userCheckRequest.getUserName())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                 String.format("%s는 중복 된 닉네임입니다.", userCheckRequest.getUserName()));
                      });

        return "사용 가능한 닉네임 입니다.";
    }

    @Transactional
    public String modifyUser(UserModifyRequest userModifyRequest, String email) {

        userRepository.findByUserName(userModifyRequest.getUserName())
                      .ifPresent(user -> { throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                                  String.format("%s는 중복 된 닉네임입니다.", userModifyRequest.getUserName()));
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
}
