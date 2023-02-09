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
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "email not found"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid password"),
    INVALID_REQUEST(HttpStatus.UNAUTHORIZED, "invalid quest"),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Username Not Found"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token invalid"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Token expired"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token not found"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Access forbidden"),
    EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "Exhibition not found"),
    GATHERING_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Gathering post not found"),
    DUPLICATED_ENROLL(HttpStatus.CONFLICT, "User is Duplicate"),
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Review Contents not Found"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error"),
    INVALID_MAIL(HttpStatus.UNAUTHORIZED, "invalid email"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Review is Not Found"),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Participant not found"),
    CONFLICT(HttpStatus.CONFLICT, "Request is Conflict"),
    ;
    private HttpStatus httpStatus;
    private String message;
}
