package com.dev.musiummate.service;

import com.dev.musiummate.configuration.EncrypterConfig;
import com.dev.musiummate.domain.UserEntity;
import com.dev.musiummate.domain.dto.user.UserDto;
import com.dev.musiummate.domain.dto.user.UserJoinRequest;
import com.dev.musiummate.domain.dto.user.UserJoinResponse;
import com.dev.musiummate.exception.AppException;
import com.dev.musiummate.exception.ErrorCode;
import com.dev.musiummate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

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
}
