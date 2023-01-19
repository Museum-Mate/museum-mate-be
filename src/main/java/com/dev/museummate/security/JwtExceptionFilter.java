package com.dev.museummate.security;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.ErrorResponse;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (MalformedJwtException e){
            log.info("손상된 토큰입니다: {}", request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        } catch (UnsupportedJwtException e){
            log.info("지원하지 않는 토큰입니다: {}", request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        } catch (SignatureException e){
            log.info("시그니처 검증에 실패한 토큰입니다: {}",request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        } catch(ExpiredJwtException e) {
            log.info("만료된 토큰입니다: {}",request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", ErrorCode.EXPIRED_TOKEN);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException | JwtException e){
            log.info("잘못된 토큰입니다: {}",request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        } catch (AppException e){   // 유저가 존재하지 않는 경우
            log.info("App Exception: {}",request.getHeader(HttpHeaders.AUTHORIZATION));
            request.setAttribute("errorCode", e.getErrorCode());
            filterChain.doFilter(request, response);
        }
    }
}
