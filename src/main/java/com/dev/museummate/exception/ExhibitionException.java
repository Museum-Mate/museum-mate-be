package com.dev.museummate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExhibitionException extends RuntimeException{

    private ErrorCode errorCode;

    private String message;

    public ExhibitionException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        if (message == null) return errorCode.getMessage();
        return String.format("%s %s", errorCode.getMessage(), message);
    }
}
