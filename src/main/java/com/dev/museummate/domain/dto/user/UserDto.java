package com.dev.museummate.domain.dto.user;

import com.dev.museummate.domain.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String providerType;

    @Builder
    public UserDto(Long id, String email, String password, String name, String userName, String birth, String phoneNumber, String address,
                   Boolean auth, String providerType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.auth = auth;
        this.providerType = providerType;
    }

    /**
     * UserEntity를 UserDto로 변환
     */
    public static UserDto toDto(UserEntity savedUser) {

        return UserDto.builder()
                      .id(savedUser.getId())
                      .email(savedUser.getEmail())
                      .name(savedUser.getName())
                      .userName(savedUser.getUserName())
                      .birth(savedUser.getBirth())
                      .address(savedUser.getAddress())
                      .phoneNumber(savedUser.getPhoneNumber())
                      .auth(savedUser.getAuth())
                      .providerType(savedUser.getProviderType())
                      .build();
    }
}
