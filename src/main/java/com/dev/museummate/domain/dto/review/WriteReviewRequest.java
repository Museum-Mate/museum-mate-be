package com.dev.museummate.domain.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteReviewRequest {

    private String title; // "test review title"
    private String content; // "test review content"
    private Integer star; // 5
    private String visitedDate; //"2023-02-17"

}
