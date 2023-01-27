package com.dev.museummate.domain.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;

public class ReviewDto {

    // fields
    private Long id;
    private String title;
    private String content;
    private Integer star;
    private Long userId; // From UserEntity
    private Long exhibitionId; // From ExhibitionEntity
    private String visitedDate;
    private LocalDateTime createdAt; // 최초 생성 일시
    private LocalDateTime lastModifiedAt; // 최종 수정 일시
    private LocalDateTime deletedAt; // 삭제 일시
    private String createdBy; // 최소 생성 사용자 userName
    private String lastModifiedBy; // 최종 수정 사용자 userName

    // Constructor : Parameter 추가 예정
    @Builder
    public ReviewDto() {
        // title, content, star, visitedDate..
    }

    /*
    from Entity to DTO
    : ReviewService - Repository 로 데이터를 찾아 Dto로 반환할 때 사용, Review Entity 추가 후 작성 예정
     */

//    public static ReviewDto toDto(ReviewEntity reviewEntity) {
//
//        return ReviewDto.builder().build();
//
//    }


}
