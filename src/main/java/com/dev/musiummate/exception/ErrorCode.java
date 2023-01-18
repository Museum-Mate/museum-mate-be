package com.dev.musiummate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "User name conflict"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "Post Not Found");

    private HttpStatus httpStatus;
    private String message;
}
