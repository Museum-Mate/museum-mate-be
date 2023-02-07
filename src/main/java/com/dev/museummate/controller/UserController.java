package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.service.MailService;
import com.dev.museummate.service.UserService;
import com.dev.museummate.utils.CookieUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import org.springframework.web.util.CookieGenerator;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MailService mailService;

    @Value("${cookie.maxage}")
    private int maxAge;

    @PostMapping("/join")
    public Response<String> join(@RequestBody UserJoinRequest userJoinRequest) throws MessagingException, UnsupportedEncodingException {
        String email = userService.join(userJoinRequest);
        String msg = mailService.sendEmail(email);
        return Response.success(msg);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request,
                                             HttpServletResponse response) {

        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        String accessToken = userLoginResponse.getAccessToken();
        String refreshToken = userLoginResponse.getRefreshToken();

        if (userLoginResponse.getAccessToken() != null) {
            log.info("헤더에 저장된 토큰 : Authorization {}", userLoginResponse.getAccessToken());
            response.setHeader("Authorization", userLoginResponse.getAccessToken());
        }

        if (userLoginResponse.getRefreshToken() != null) {
            log.info("쿠키에 저장된 토큰 : refreshToken {}", userLoginResponse.getRefreshToken());
            ResponseCookie cookie = ResponseCookie.from("refeshToken", refreshToken)
                                                  .httpOnly(true)
                                                  .secure(true)
                                                  .sameSite("Lax")
                                                  .path("/")
                                                  .maxAge(maxAge)
                                                  .build();
            log.debug("method: createCooke cookie: {}", cookie.toString());

            // 헤더에 Set-Cookie 를 추가
            response.addHeader("Set-Cookie", cookie.toString());

//            CookieUtils.addCookie(response, "refreshToken", refreshToken, maxAge);
        }

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
