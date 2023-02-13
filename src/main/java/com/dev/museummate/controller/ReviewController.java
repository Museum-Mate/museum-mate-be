package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.Response;
import com.dev.museummate.domain.dto.review.DeleteReviewResponse;
import com.dev.museummate.domain.dto.review.EditReviewRequest;
import com.dev.museummate.domain.dto.review.EditReviewResponse;
import com.dev.museummate.domain.dto.review.GetReviewResponse;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.ReviewPageResponse;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @DeleteMapping("/{reviewId}")
  public Response deleteReview(@PathVariable Long reviewId, Authentication authentication) {

    ReviewDto deletedReviewDto = reviewService.deleteReview(reviewId,
                                                            authentication.getName(),
                                                            authentication.getAuthorities()); // 서비스에서 삭제 후 반환한 dto

    DeleteReviewResponse deleteReviewResponse = DeleteReviewResponse.toResponse(deletedReviewDto); // DTO -> Response

    return Response.success(deleteReviewResponse);

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
  
  // [X] 리뷰 상세 조회
  @GetMapping("/{reviewId}/details")
  public Response<GetReviewResponse> getReview(@PathVariable Long reviewId) {
    // 리뷰 서비스를 통해 reviewId로 리뷰 객체를 얻어온다.
    ReviewDto selectedReview = reviewService.getReview(reviewId);

    // 리뷰 조회 객체를 생성한다.
    GetReviewResponse getReviewResponse = GetReviewResponse.toResponse(selectedReview);

    // 반환
    return Response.success(getReviewResponse);
  }

  // 리뷰 리스트 조회
  @GetMapping("/{exhibitionId}/reviews")
  public Response<ReviewPageResponse> getReviewList(Pageable pageable,
                                                    @PathVariable Long exhibitionId) {
    // 리뷰 서비스를 통해 ReviewDto로 묶인 Page 객체 생성
    Page<ReviewDto> reviewDtos = reviewService.getAllReviews(exhibitionId, pageable);

    // 리뷰 리스트 리스판스 객체 생성
    ReviewPageResponse reviewPageResponse = new ReviewPageResponse(reviewDtos.getContent(), pageable);

    // 반환
    return Response.success(reviewPageResponse);
  }

}
