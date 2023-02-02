package com.dev.museummate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.review.DeleteReviewResponse;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.ReviewEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.fixture.UserEntityFixture;
import com.dev.museummate.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
import org.hibernate.sql.Delete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ReviewService reviewService;

    private ExhibitionEntity exhibitionEntity;

    @BeforeEach
    void setUp() {

        exhibitionEntity = ExhibitionEntity.builder()
                .id(1L)
                .name("test exhibition")
                .startsAt("2023-01-01")
                .endsAt("2023-02-28")
                .ageLimit("30세")
                .price("무료")
                .detailInfo("test detail info")
                .gallery(
                        new GalleryEntity(
                                1L,
                                "test gallery",
                                "seoul ddandong",
                                "00:00",
                                "24:00" )
                        ).build();
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    @WithMockUser
    void writeReview() throws Exception {

        /*
        테스트 로직
        1. 짭 WriteReviewRequest 객체 생성
        2. 요청 객체의 데이터를 ReviewDto 객체로 전이?
        3. 행동 가정 (reviewService.writeReview~)
        4. mockMvc.perform 수행 시 예측 결과가 나오는지 확인
         */

        WriteReviewRequest writeReviewRequest = WriteReviewRequest.builder()
                .title("test review title")
                .content("test review content")
                .star(3)
                .visitedDate("2023-02-17")
                .build();

        UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");

        ReviewEntity testReview = ReviewEntity.builder()
                .id(1L)
                .title(writeReviewRequest.getTitle())
                .content(writeReviewRequest.getContent())
                .star(writeReviewRequest.getStar())
                .user(testUser)
                .exhibition(exhibitionEntity)
                .visitedDate(writeReviewRequest.getVisitedDate())
                .build();

        ReviewDto reviewDto = ReviewDto.toDto(testReview);

        WriteReviewResponse writeReviewResponse = WriteReviewResponse.fromDtoToResponse(reviewDto);

        when(reviewService.writeReview(any(), any(), any()))
                .thenReturn(reviewDto);

        mockMvc.perform(post("/api/v1/reviews/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(writeReviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 로그인 정보 없음 ")
    @WithAnonymousUser
    void writeReview_fail_1() throws Exception {

        WriteReviewRequest writeReviewRequest = WriteReviewRequest.builder()
                .title("test review title")
                .content("test review content")
                .star(3)
                .visitedDate("2023-02-17")
                .build();

        when(reviewService.writeReview(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/reviews/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(writeReviewRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("리뷰 등록 실패 - 리뷰 content 없음")
    @WithMockUser
    void writeReview_fail_2() throws Exception {
        WriteReviewRequest writeReviewRequest = WriteReviewRequest.builder()
                .title("test review title")

                .star(3)
                .visitedDate("2023-02-17")
                .build();

        UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");

        when(reviewService.writeReview(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.CONTENT_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/reviews/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(writeReviewRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Nested
    @DisplayName("리뷰 삭제")
    @WithMockUser
    class deleteTest {
        @Test
        @DisplayName("리뷰 삭제 성공")
        void deleteReview_success() throws Exception {
            /*
            테스트 로직
            1.
            2.
            3. 행동 가정 (reviewService.deleteReview~)
            4. mockMvc.perform 수행 시 예측 결과가 나오는지 확인
            */
            UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");

            ReviewEntity reviewEntity = ReviewEntity.builder()
                .id(2L)
                .title("삭제 테스트용 제목")
                .content("삭제 테스트용 내용")
                .star(3)
                .user(testUser)
                .exhibition(exhibitionEntity)
                .visitedDate("1901-01-01")
                .isDeleted(Boolean.FALSE)
                                                    .build();

            ReviewDto deletedReviewDto = ReviewDto.toDto(reviewEntity);

//            DeleteReviewResponse deleteReviewResponse = DeleteReviewResponse.builder()
//                .reviewId(2L)
//                .message("리뷰 삭제")
//                                                                            .build();

            when(reviewService.deleteReview(anyLong(), anyString(), anyCollection())).thenReturn(deletedReviewDto);

            mockMvc.perform(delete("/api/v1/reviews/2")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.reviewId").value(2))
                .andDo(print());
        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 행위자와 리뷰작성자 불일치 ")
        void deleteReview_fail1() throws Exception {

            UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");

            ReviewEntity reviewEntity = ReviewEntity.builder()
                                                    .id(2L)
                                                    .title("삭제 테스트용 제목")
                                                    .content("삭제 테스트용 내용")
                                                    .star(3)
                                                    .user(testUser)
                                                    .exhibition(exhibitionEntity)
                                                    .visitedDate("1901-01-01")
                                                    .isDeleted(Boolean.FALSE)
                                                    .build();

            when(reviewService.deleteReview(anyLong(), anyString(), anyCollection())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자 불일치"));

            mockMvc.perform(delete("/api/v1/reviews/2")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isUnauthorized())
                   .andDo(print());
        }

        @Test
        @DisplayName("리뷰 삭제 실패 - DB Error ")
        void deleteReview_fail2() throws Exception {

            UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");

            ReviewEntity reviewEntity = ReviewEntity.builder()
                                                    .id(2L)
                                                    .title("삭제 테스트용 제목")
                                                    .content("삭제 테스트용 내용")
                                                    .star(3)
                                                    .user(testUser)
                                                    .exhibition(exhibitionEntity)
                                                    .visitedDate("1901-01-01")
                                                    .isDeleted(Boolean.FALSE)
                                                    .build();

            when(reviewService.deleteReview(anyLong(), anyString(), anyCollection())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "예상치 못한 에러가 발생했습니다."));

            mockMvc.perform(delete("/api/v1/reviews/2")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isInternalServerError())
                   .andDo(print());
        }


    }

}