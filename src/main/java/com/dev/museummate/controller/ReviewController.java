package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.service.ExhibitionService;
import com.dev.museummate.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    // private final ExhibitionService exhibitionService;
    private final ReviewService reviewService;

    /*
    [X] 리뷰 등록
     */
    @PostMapping("/{exhibitionId}")
    public Response<WriteReviewResponse> writeReview(@RequestBody WriteReviewRequest writeReviewRequest,
                                                     @PathVariable Long exhibitionId,
                                                     Authentication authentication) {
        // authentication에서 name 추출
        String email = authentication.getName();

        // review service를 통해 review dto 생성
        WriteReviewResponse writeReviewResponse =
                reviewService.writeReview(email, writeReviewRequest, exhibitionId);

        // writeReviewResponse 생성
        return Response.success(writeReviewResponse);
    }

    /*
    [ ] 리뷰 조회
     */


    /*
    [ ] 리뷰 수정
     */


    /*
    [ ] 리뷰 삭제
     */

}
