package com.dev.museummate.service;

import com.dev.museummate.domain.UserEntity;
import com.dev.museummate.domain.dto.user.UserDto;
import com.dev.museummate.domain.dto.user.UserJoinRequest;
import com.dev.museummate.domain.dto.user.UserJoinResponse;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.UserRepository;
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
