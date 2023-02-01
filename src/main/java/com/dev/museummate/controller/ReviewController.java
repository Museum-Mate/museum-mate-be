package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.review.EditReviewRequest;
import com.dev.museummate.domain.dto.review.EditReviewResponse;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

  private final ReviewService reviewService;

  /*
  [X] 리뷰 등록, WriteReviewResponse 반환
   */
  @PostMapping("/{exhibitionId}")
  public Response<WriteReviewResponse> writeReview(@RequestBody WriteReviewRequest writeReviewRequest,
      @PathVariable Long exhibitionId,
      Authentication authentication) {
    // authentication에서 name 추출
    String email = authentication.getName();

    // review service를 통해 review dto 생성
    ReviewDto savedReviewDto =
        reviewService.writeReview(email, writeReviewRequest, exhibitionId);

    // From savedReviewDto to WriteReviewResponse
    WriteReviewResponse writeReviewResponse = WriteReviewResponse.fromDtoToResponse(savedReviewDto);

    // writeReviewResponse 반환
    return Response.success(writeReviewResponse);
  }

  /*
  [] 리뷰 수정
   */
  @PutMapping("/{reviewId}")
  public Response<EditReviewResponse> editReview(@RequestBody EditReviewRequest editReviewRequest,
                                                 @PathVariable Long reviewId,
                                                 Authentication authentication) {

    // authentication에서 name 추출
    String email = authentication.getName();

    // Service Layer로 전달하여 수정 로직 결과를 Dto로 받아온다.
    ReviewDto editedReview = reviewService.editReview(email, editReviewRequest, reviewId);

    // Convert Dto to Response
    EditReviewResponse editReviewResponse = EditReviewResponse.fromDtoToResponse(editedReview);

    return Response.success(editReviewResponse);

  }
  

}
