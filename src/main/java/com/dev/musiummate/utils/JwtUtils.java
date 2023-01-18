package com.dev.musiummate.utils;

import com.dev.musiummate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserService userService;

    public static String createToken(String userName, String secretKey, long expiredTimeMs){
        return "";
    }
}
