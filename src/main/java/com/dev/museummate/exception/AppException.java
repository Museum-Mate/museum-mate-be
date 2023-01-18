package com.dev.museummate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        } else {
            return String.format("%s: %s", errorCode.getMessage(), message);
        }

    }
}
