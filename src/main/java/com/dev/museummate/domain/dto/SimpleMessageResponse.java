package com.dev.museummate.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessageResponse {

    private String message;

    public static SimpleMessageResponse of(String message) {
        return SimpleMessageResponse.builder()
                                    .message(message)
                                    .build();
    }
}
