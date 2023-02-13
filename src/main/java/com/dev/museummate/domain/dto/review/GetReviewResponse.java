package com.dev.museummate.domain.dto.review;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetReviewResponse {
  // fields
  Long id;
  String title;
  String content;
  Integer star;
  String userName; // 작성자 닉네임
  String exhibitionName; // 전시 제목
  String visitedDate;
  LocalDateTime createdAt;
  LocalDateTime lastModifiedAt;
  String createdBy;
  String lastModifiedBy;

  // Constructor Using Builder Pattern
  @Builder
  public GetReviewResponse(Long id, String title, String content, Integer star, String userName,
                           String exhibitionName, String visitedDate, LocalDateTime createdAt,
                           LocalDateTime lastModifiedAt, String createdBy, String lastModifiedBy) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.star = star;
    this.userName = userName;
    this.exhibitionName = exhibitionName;
    this.visitedDate = visitedDate;
    this.createdAt = createdAt;
    this.lastModifiedAt = lastModifiedAt;
    this.createdBy = createdBy;
    this.lastModifiedBy = lastModifiedBy;
  }

  // From Dto to Response
  public static GetReviewResponse toResponse(ReviewDto reviewDto) {
    return new GetReviewResponse(
        reviewDto.getId(), reviewDto.getTitle(), reviewDto.getContent(), reviewDto.getStar(),
        reviewDto.getUserName(), reviewDto.getExhibitionName(), reviewDto.getVisitedDate(),
        reviewDto.getCreatedAt(), reviewDto.getLastModifiedAt(), reviewDto.getCreatedBy(),
        reviewDto.getLastModifiedBy()
    );
  }

}
