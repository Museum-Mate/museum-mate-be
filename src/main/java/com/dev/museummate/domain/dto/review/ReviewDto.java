package com.dev.museummate.domain.dto.review;

import com.dev.museummate.domain.entity.ReviewEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // Constructor
    @Builder
    public ReviewDto(Long id, String title, String content,
                     Integer star, Long userId, Long exhibitionId,
                     String visitedDate,
                     LocalDateTime createdAt,
                     LocalDateTime lastModifiedAt,
                     LocalDateTime deletedAt,
                     String createdBy,
                     String lastModifiedBy) {
        // title, content, star, visitedDate..
        this.id = id;
        this.title = title;
        this.content = content;
        this.star = star;
        this.userId = userId;
        this.exhibitionId = exhibitionId;
        this.visitedDate = visitedDate;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    /*
    from Entity to DTO
    : ReviewService - Repository 로 데이터를 찾아 Dto로 반환할 때 사용, Review Entity 추가 후 작성 예정
     */
    public static ReviewDto toDto(ReviewEntity reviewEntity) {

        return new ReviewDto(
                reviewEntity.getId(),
                reviewEntity.getTitle(),
                reviewEntity.getContent(),
                reviewEntity.getStar(),
                reviewEntity.getUser().getId(),
                reviewEntity.getExhibition().getId(),
                reviewEntity.getVisitedDate(),
                reviewEntity.getCreatedAt(),
                reviewEntity.getLastModifiedAt(),
                reviewEntity.getDeletedAt(),
                reviewEntity.getCreatedBy(),
                reviewEntity.getLastModifiedBy()
        );

    }


}
