package com.dev.musiummate.controller;

import com.dev.musiummate.configuration.Response;
import com.dev.musiummate.domain.dto.user.UserJoinRequest;
import com.dev.musiummate.domain.dto.user.UserJoinResponse;
import com.dev.musiummate.service.UserService;
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


}
