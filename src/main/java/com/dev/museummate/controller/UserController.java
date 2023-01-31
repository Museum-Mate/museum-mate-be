package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.service.MailService;
import com.dev.museummate.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/join")
    public Response<String> join(@RequestBody UserJoinRequest userJoinRequest) throws MessagingException, UnsupportedEncodingException {
        String email = userService.join(userJoinRequest);
        String msg = mailService.sendEmail(email);
        return Response.success(msg);
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

    @PostMapping("/check")
    public Response<String> check(@RequestBody UserCheckRequest userCheckRequest) {
        String msg = userService.userNameCheck(userCheckRequest);
        return Response.success(msg);
    }

    @PutMapping("/modify")
    public Response<String> modify(@RequestBody UserModifyRequest userModifyRequest,Authentication authentication) {
        String msg = userService.modifyUser(userModifyRequest, authentication.getName());
        return Response.success(msg);
    }

    @DeleteMapping("/delete")
    public Response<String> delete(Authentication authentication) {
        String msg = userService.deleteUser(authentication.getName());
        return Response.success(msg);
    }

    @GetMapping("/auth")
    public Response<String> auth(@RequestParam("authNum") String authNum, @RequestParam("email") String email) {
        String msg = userService.auth(authNum, email);
        return Response.success(msg);
    }

}
