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
                                                                                  String.format("%s?????? ???????????? ????????????.", email)));
    }

    @Transactional
    public String join(UserJoinRequest userJoinRequest) {
        log.info("???????????? ?????? : {}", userJoinRequest);

        userRepository.findByUserName(userJoinRequest.getUserName())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                 String.format("%s??? ?????? ??? ??????????????????.", userJoinRequest.getUserName()));
                      });

        userRepository.findByEmail(userJoinRequest.getEmail())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_EMAIL, String.format("%s??? ?????? ??? ??????????????????.", userJoinRequest.getEmail()));
                      });

        UserEntity user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));
        UserEntity savedUser = userRepository.save(user);

        UserDto userDto = UserDto.toDto(savedUser);
        log.info("???????????? ??????!");
        log.info("email: {}, name: {}, userName: {}", savedUser.getEmail(), savedUser.getName(), savedUser.getUserName());

        return userDto.getEmail();
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        UserEntity findUser = findUserByEmail(userLoginRequest.getEmail());

        if (!encoder.matches(userLoginRequest.getPassword(), findUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "????????? ???????????? ?????????.");
        }
        if (findUser.getAuth().equals(Boolean.FALSE)) {
            throw new AppException(ErrorCode.INVALID_MAIL, "?????? ?????? ?????? ????????? ?????????.");
        }

        String accessToken = jwtUtils.createAccessToken(userLoginRequest.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(userLoginRequest.getEmail());

        // ?????? ?????? {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findUser.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new UserLoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public UserLoginResponse reissue(UserTokenRequest userTokenRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        if (jwtUtils.isExpired(userTokenRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "????????? ???????????????.");
        }

        String refreshToken = redisDao.getValues("RT:" + email);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "????????? ???????????????");
        }
        if (!refreshToken.equals(userTokenRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "????????? ???????????????.");
        }

        String newAccessToken = jwtUtils.createAccessToken(findUser.getEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(findUser.getEmail());

        // ?????? ?????? {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findUser.getEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new UserLoginResponse(newAccessToken, refreshToken);

    }

    public String logout(UserTokenRequest userTokenRequest, String email) {

        UserEntity findUser = findUserByEmail(email);

        String accessToken = userTokenRequest.getAccessToken();

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "????????? ???????????????.");
        }

        if (!jwtUtils.isValid(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "????????? ???????????????.");
        }

        // Token ??????
        redisDao.deleteValues("RT:" + findUser.getEmail());

        int expiration = jwtUtils.getExpiration(userTokenRequest.getAccessToken()).intValue() / 1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(userTokenRequest.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return "???????????? ???????????????.";
    }

    public String userNameCheck(UserCheckRequest userCheckRequest) {
        userRepository.findByUserName(userCheckRequest.getUserName())
                      .ifPresent(user -> {
                          throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                 String.format("%s??? ?????? ??? ??????????????????.", userCheckRequest.getUserName()));
                      });

        return "?????? ????????? ????????? ?????????.";
    }

    @Transactional
    public String modifyUser(UserModifyRequest userModifyRequest, String email) {

        userRepository.findByUserName(userModifyRequest.getUserName())
                      .ifPresent(user -> { throw new AppException(ErrorCode.DUPLICATE_USERNAME,
                                                                  String.format("%s??? ?????? ??? ??????????????????.", userModifyRequest.getUserName()));
                      });

        UserEntity findUser = findUserByEmail(email);

        findUser.updateInfo(userModifyRequest);

        userRepository.save(findUser);

        return "????????? ?????? ???????????????.";
    }

    public String deleteUser(String email) {

        UserEntity findUser = findUserByEmail(email);

        userRepository.delete(findUser);

        return "????????? ?????? ???????????????.";
    }

    @Transactional
    public String auth(String authNum, String email) {
        UserEntity findUser = findUserByEmail(email);

        if (findUser.getAuthNum().equals(authNum)) {
            findUser.updateAuth();
            userRepository.save(findUser);
            return "????????? ?????? ???????????????.";
        }
        return "????????? ?????? ????????????.";
    }
}
