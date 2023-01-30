package com.dev.museummate.domain.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WriteReviewResponse {
    private Long id; // review_id
    private String message; // 리뷰 등록 성공
    //String title;
    //String content;
}
