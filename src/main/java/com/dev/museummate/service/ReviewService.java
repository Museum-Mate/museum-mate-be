package com.dev.museummate.service;

import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    //private final ReviewRepository reviewRepository;

    /*
    Returns Response
     */
    public WriteReviewResponse writeReview(String name,
                                           WriteReviewRequest writeReviewRequest,
                                           Long exhibitionId) {
        // 유저 검증 (한 번 더 하는 것)
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "Username Not Found"));

        // 전시회 검증
        ExhibitionEntity exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "Exhibition not found"));


        // 리뷰 등록
        /*
        로직
        1. ReviewEntity 생성
        2. ReviewEntity toDto 메서드로 Dto 객체 생성하여 Response 객체로 전달
         */

        return new WriteReviewResponse();
    }
}
