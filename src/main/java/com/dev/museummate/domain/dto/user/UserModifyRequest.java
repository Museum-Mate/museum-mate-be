package com.dev.museummate.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyRequest {

    private String phoneNumber;
    private String address;
    private String userName;

}
