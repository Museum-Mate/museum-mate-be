package com.dev.museummate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "User name conflict"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "email conflict"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "Post Not Found"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND,"email not found"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid password"),
    ;
    private HttpStatus httpStatus;
    private String message;
}
