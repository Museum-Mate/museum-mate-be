package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserDto userDto = userService.join(userJoinRequest);
        return Response.success(new UserJoinResponse(userDto.getUserName()));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);
        return Response.success(userLoginResponse);
    }

    @PostMapping("/reissue")
    public Response<UserLoginResponse> reissue(@RequestBody UserReissueRequest userReissueRequest,Authentication authentication) {
        UserLoginResponse userLoginResponse = userService.reissue(userReissueRequest, authentication.getName());
        return Response.success(userLoginResponse);
    }

    @PostMapping("/logout")
    public Response<String> logout(@RequestBody UserReissueRequest userLogoutRequest,Authentication authentication) {
        String msg = userService.logout(userLogoutRequest, authentication.getName());
        return Response.success(msg);
    }


}
