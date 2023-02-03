package com.dev.museummate.service;

import com.dev.museummate.domain.dto.review.EditReviewRequest;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.ReviewEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.ReviewRepository;
import com.dev.museummate.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewRepository reviewRepository;

    /*
    Service Layer returns Dto to Controller Layer
     */
    public ReviewDto writeReview(String email,
                                           WriteReviewRequest writeReviewRequest,
                                           Long exhibitionId) {
        // 유저 검증 (한 번 더 하는 것)
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("Username %s Not Found.😢", email)));

        // 전시회 검증
        ExhibitionEntity exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "Exhibition not found"));


        // 리뷰 등록
        // ReviewEntity 생성
        ReviewEntity review = ReviewEntity.builder()
                .title(writeReviewRequest.getTitle())
                .content(writeReviewRequest.getContent())
                .star(writeReviewRequest.getStar())
                .visitedDate(writeReviewRequest.getVisitedDate())
                .user(user)
                .exhibition(exhibition)
                .build();

        // db에 review를 저장
        reviewRepository.save(review);

        // ReviewDto 생성
        ReviewDto savedReviewDto = ReviewDto.toDto(review);

        return savedReviewDto;
    }

    /*
    리뷰 수정 로직
     */
    public ReviewDto editReview(String authEmail,
                                EditReviewRequest editReviewRequest,
                                Long reviewId) {
        // 유저 검증
        UserEntity user = userRepository.findByEmail(authEmail)
                                        .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "Username Not Found."));

        // 리뷰 검증
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                                                    .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND, "Review Not Found."));

        // 리뷰 작성자가 유저인지 검증
        if (!Objects.equals(reviewEntity.getUser().getEmail(), authEmail)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        // request를 entity로 변경
        ReviewEntity editRequestToEntity = reviewEntity.toEntity(editReviewRequest);

        // ReviewEntity 수정
        reviewEntity.editReview(editRequestToEntity);

        reviewEntity = reviewRepository.save(reviewEntity);

        // 변경된 ReviewDto 생성
        ReviewDto editedReviewDto = ReviewDto.toDto(reviewEntity);

        // 변경된 ReviewDto 반환
        return editedReviewDto;
    }

    // 리뷰 상세 조회
    public ReviewDto getReview(Long reviewId) {

        ReviewEntity review = reviewRepository.findById(reviewId)
                                              .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, String.format("ReviewId #%d Not found", reviewId)));

        ReviewDto reviewDto = ReviewDto.toDto(review);

        return reviewDto;
    }
}
