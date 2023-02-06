package com.dev.museummate.domain.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.dev.museummate.domain.entity.UserEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;
    private Boolean auth;

    @Builder
    public UserDto(Long id, String email, String password, String name, String userName, String birth, String phoneNumber, String address, Boolean auth) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.auth = auth;
    }

    /**
     * UserEntity를 UserDto로 변환
     */
    public static UserDto toDto(UserEntity savedUser) {

        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .name(savedUser.getName())
                .userName(savedUser.getUserName())
                .birth(savedUser.getBirth())
                .phoneNumber(savedUser.getPhoneNumber())
                .auth(savedUser.getAuth())
                .build();
    }
}
