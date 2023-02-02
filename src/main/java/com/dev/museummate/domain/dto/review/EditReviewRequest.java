package com.dev.museummate.domain.dto.review;

import com.dev.museummate.domain.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditReviewRequest {

    private String newTitle; // "test review title" -> "Test Edit Review Title"
    private String newContent; // "test review content" "Test Edit Review Content"
    private Integer newStar; // 5 -> 0
    private String newVisitedDate; // "2023-02-17" -> "2022-02-17"

    // Request to Entity
    public ReviewEntity toEntity(EditReviewRequest editReviewRequest) {
        return ReviewEntity.builder()
            .title(editReviewRequest.getNewTitle())
            .content(editReviewRequest.getNewContent())
            .star(editReviewRequest.getNewStar())
            .visitedDate(editReviewRequest.getNewVisitedDate())
                           .build();
    }

}