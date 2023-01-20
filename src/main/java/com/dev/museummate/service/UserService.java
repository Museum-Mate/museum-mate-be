package com.dev.museummate.service;

import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private long accessExpireTimeMs = 1000 * 60 * 5;

    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_FOUND, String.format("%s님은 존재하지 않습니다.",email)));
    }

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {

        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user ->{
                    throw new AppException(ErrorCode.DUPLICATE_USERNAME,String.format("%s는 중복 된 유저네임입니다.",userJoinRequest.getUserName()));
                });

        userRepository.findByEmail(userJoinRequest.getAddress())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_EMAIL,String.format("%s는 중복 된 이메일입니다.",userJoinRequest.getEmail()));
                });

        UserEntity user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));
        UserEntity savedUser = userRepository.save(user);

        UserDto userDto = UserDto.toDto(savedUser);

        return new UserJoinResponse(userDto.getUserName());

    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        UserEntity findUser = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND,
                        String.format("%s는 없는 계정입니다.", userLoginRequest.getEmail())));

        if(!encoder.matches(userLoginRequest.getPassword(), findUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,String.format("잘못된 비밀번호 입니다."));
        }

        String accessToken = JwtUtils.createAccessToken(userLoginRequest.getEmail(), secretKey, accessExpireTimeMs);

        return new UserLoginResponse(accessToken);

    }


}
