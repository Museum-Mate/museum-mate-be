package com.dev.musiummate.domain.dto.user;

import com.dev.musiummate.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;

    @Builder
    public UserDto(Long id, String email, String password, String userName, String birth, String phoneNumber, String address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static UserDto toDto(UserEntity savedUser) {

        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getPassword())
                .password(savedUser.getPassword())
                .userName(savedUser.getUserName())
                .birth(savedUser.getBirth())
                .phoneNumber(savedUser.getPhoneNumber())
                .build();
    }
}
