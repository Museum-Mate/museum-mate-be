package com.dev.museummate.domain.dto.review;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditReviewResponse {

    private Long id; // review_id
    private String title;
    private String content;
    private Integer star;
    private String userName; // From UserEntity
    private String exhibitionName; // From ExhibitionEntity
    private String visitedDate;
    private LocalDateTime createdAt; // 최초 생성 일시
    private LocalDateTime lastModifiedAt; // 최종 수정 일시
    private String createdBy; // 최소 생성 사용자 userName
    private String lastModifiedBy; // 최종 수정 사용자 userName

    public static EditReviewResponse fromDtoToResponse(ReviewDto reviewDto) {
        return new EditReviewResponse(
            reviewDto.getId(),
            reviewDto.getTitle(),
            reviewDto.getContent(),
            reviewDto.getStar(),
            reviewDto.getUserName(),
            reviewDto.getExhibitionName(),
            reviewDto.getVisitedDate(),
            reviewDto.getCreatedAt(),
            reviewDto.getLastModifiedAt(),
            reviewDto.getCreatedBy(),
            reviewDto.getLastModifiedBy()
        );
    }
}
