package com.dev.museummate.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {

    private String resultCode;
    private T result;

    public static <T> Response error(T data) {
        return new Response("ERROR", data);
    }

    public static <T> Response success(T data) {
        return new Response("SUCCESS", data);
    }
}
