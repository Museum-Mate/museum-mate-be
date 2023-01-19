package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.user.UserJoinRequest;
import com.dev.museummate.domain.dto.user.UserJoinResponse;
import com.dev.museummate.domain.dto.user.UserLoginRequest;
import com.dev.museummate.domain.dto.user.UserLoginResponse;
import com.dev.museummate.service.UserService;
import lombok.RequiredArgsConstructor;
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


}
