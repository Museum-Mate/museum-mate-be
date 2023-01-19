package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest);
        return Response.success(userJoinResponse);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);
        return Response.success(userLoginResponse);
    }

    @PostMapping("/reissue")
    public Response<UserLoginResponse> reissue(@RequestBody UserReissueRequest userReissueRequest) {
        UserLoginResponse userLoginResponse = userService.reissue(userReissueRequest, "chlalsnws600naver.com");
        return Response.success(userLoginResponse);
    }

    @PostMapping("/logout")
    public Response<String> logout(@RequestBody UserReissueRequest userLogoutRequest) {
        String msg = userService.logout(userLogoutRequest, "chlalsnws600naver.com");
        return Response.success(msg);
    }


}
