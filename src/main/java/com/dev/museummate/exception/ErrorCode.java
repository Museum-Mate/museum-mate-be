package com.dev.museummate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "User name conflict"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "email conflict"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND,"email not found"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid password"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다" ),
    ;

    private HttpStatus httpStatus;
    private String message;
}
