package com.dev.museummate.service;

import com.dev.museummate.domain.UserRole;
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
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
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

    // [] 리뷰 삭제
    public ReviewDto deleteReview(Long reviewId, String userEmail, Collection<? extends GrantedAuthority> userRole) {

        UserEntity reviewer = userRepository.findByEmail(userEmail) // 유저 검증
                                        .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("Username %s Not Found.", userEmail)));

        ReviewEntity reviewEntity = reviewRepository.findById(reviewId) // 리뷰 검증
                                                    .orElseThrow(()
                                                                     -> new AppException(ErrorCode.CONTENT_NOT_FOUND, String.format("Content %s Not Found.", reviewId)));

        if(!Objects.equals(reviewEntity.getUser().getId(), reviewer.getId()) &&
            !userRole.stream().findFirst().get().getAuthority().equals(UserRole.ROLE_ADMIN.toString())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, ""); // 리뷰 작성자 일치 여부 and 삭제자 권한 ADMIN 여부 검증
        }

        reviewRepository.delete(reviewEntity); //검증 통과 시 삭제

        ReviewDto deletedReview = ReviewDto.builder() // 삭제 완료한 리뷰 번호로 DTO 생성하여 컨트롤러 레이어로 반환
            .id(reviewId)
            .build();

        return deletedReview;
    }
}
