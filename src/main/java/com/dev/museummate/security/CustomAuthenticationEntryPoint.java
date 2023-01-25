package com.dev.museummate.security;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.ErrorResponse;
import com.dev.museummate.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("errorCode");

        if(errorCode == null){
            errorCode = ErrorCode.TOKEN_NOT_FOUND;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        Response errorResponse = Response.error(new ErrorResponse(errorCode.toString(), errorCode.getMessage()));

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
