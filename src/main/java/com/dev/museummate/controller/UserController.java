package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.Response;
import com.dev.museummate.domain.dto.SimpleMessageResponse;
import com.dev.museummate.domain.dto.user.UserCheckRequest;
import com.dev.museummate.domain.dto.user.UserJoinRequest;
import com.dev.museummate.domain.dto.user.UserLoginRequest;
import com.dev.museummate.domain.dto.user.UserLoginResponse;
import com.dev.museummate.domain.dto.user.UserModifyRequest;
import com.dev.museummate.domain.dto.user.UserTokenRequest;
import com.dev.museummate.global.utils.CookieUtils;
import com.dev.museummate.service.MailService;
import com.dev.museummate.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

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

        if (userLoginResponse.getRefreshToken() != null) {
            log.info("쿠키에 저장된 AccessToken :");
            log.info("Authorization = {};", accessToken);
            CookieUtils.addAccessTokenAtCookie(response, accessToken);
        }

        if (userLoginResponse.getRefreshToken() != null) {
            log.info("쿠키에 저장된 RefreshToken :");
            log.info("Authorization-refresh= {}; Path=/; Secure; HttpOnly; Expires=DOW, DAY MONTH YEAR HH:MM:SS GMT;", refreshToken);
            CookieUtils.addRefreshTokenAtCookie(response, refreshToken);
        }

        return Response.success(userLoginResponse);
    }

    @PostMapping("/reissue")
    public Response<UserLoginResponse> reissue(@RequestBody UserTokenRequest userTokenRequest, Authentication authentication) {
        UserLoginResponse userLoginResponse = userService.reissue(userTokenRequest, authentication.getName());
        return Response.success(userLoginResponse);
    }

    @PostMapping("/logout")
    public Response<SimpleMessageResponse> logout(@RequestBody UserTokenRequest userLogoutRequest, Authentication authentication) {
        String msg = userService.logout(userLogoutRequest, authentication.getName());
        return Response.success(new SimpleMessageResponse(msg));
    }

    @PostMapping("/check")
    public Response<SimpleMessageResponse> check(@RequestBody UserCheckRequest userCheckRequest) {
        String msg = userService.userNameCheck(userCheckRequest);
        return Response.success(new SimpleMessageResponse(msg));
    }

    @PutMapping
    public Response<SimpleMessageResponse> modify(@RequestBody UserModifyRequest userModifyRequest, Authentication authentication) {
        String msg = userService.modifyUser(userModifyRequest, authentication.getName());
        return Response.success(new SimpleMessageResponse(msg));
    }

    @DeleteMapping
    public Response<SimpleMessageResponse> delete(Authentication authentication) {
        String msg = userService.deleteUser(authentication.getName());
        return Response.success(new SimpleMessageResponse(msg));
    }

    @GetMapping("/auth")
    public Response<SimpleMessageResponse> auth(HttpServletResponse response, @RequestParam("authNum") String authNum, @RequestParam("email") String email)
        throws IOException {
        String msg = userService.auth(authNum, email);

        response.sendRedirect("http://www.withmuma.com");

        return Response.success(new SimpleMessageResponse(msg));
    }
}
