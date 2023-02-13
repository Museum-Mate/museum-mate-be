package com.dev.museummate.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.dto.review.EditReviewRequest;
import com.dev.museummate.domain.dto.review.GetReviewResponse;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.review.ReviewPageResponse;
import com.dev.museummate.domain.dto.review.WriteReviewRequest;
import com.dev.museummate.domain.dto.review.WriteReviewResponse;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.ReviewEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.fixture.UserEntityFixture;
import com.dev.museummate.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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

    Long reviewId = 1L;

    // editReviewRequest
    EditReviewRequest editReviewRequest = EditReviewRequest.builder()
                                                           .newTitle("수정한 리뷰 제목")
                                                           .newContent("수정한 리뷰 내용")
                                                           .newStar(3)
                                                           .newVisitedDate("2022-05-05")
                                                           .build();
    // editReviewResponse

    // writeReviewRequest
    // writeReviewResponse

    @BeforeEach
    void setUp() {

        exhibitionEntity = ExhibitionEntity.builder()
                                           .id(1L)
                                           .name("test exhibition")
                                           .startAt("2023-01-01")
                                           .endAt("2023-02-28")
                                           .ageLimit("30세")
                                           .price("무료")
                                           .detailInfo("test detail info")
                                           .galleryName("test gallery")
                                           .build();
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
                                              .isDeleted(false)
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
               .andExpect(status().is3xxRedirection()) // 인증 실패로 인해서 Spring Security에서 리다이렉트 -> 302
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

            when(reviewService.deleteReview(anyLong(), anyString(), anyCollection())).thenThrow(
                new AppException(ErrorCode.INVALID_PERMISSION, "작성자 불일치"));

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

            when(reviewService.deleteReview(anyLong(), anyString(), anyCollection())).thenThrow(
                new AppException(ErrorCode.DATABASE_ERROR, "예상치 못한 에러가 발생했습니다."));

            mockMvc.perform(delete("/api/v1/reviews/2")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isInternalServerError())
                   .andDo(print());
        }


    }

    /*
리뷰 수정 컨트롤러 테스트 로직
1. 준비 작업
- 엔티티 생성 (User, Exhibition, Review)
- EditReviewRequest 객체 생성
- ReviewDto 생성
2. 행동 가정 (reviewService.editReview~)
3. mockMvc.perform 수행 시 예측 결과가 나오는지 확인
*/
    @Test
    @WithMockUser
    @DisplayName("리뷰 수정 성공")
    void edit_review_success() throws Exception {
        //테스트용 유저
        UserEntity testUser = UserEntityFixture.getUser("test@mail.com", "password");
        // 테스트용 리뷰
        ReviewEntity testReview = ReviewEntity.builder()
                                              .id(1L)
                                              .title("기존 리뷰")
                                              .content("기존 리뷰 내용")
                                              .star(5)
                                              .user(testUser)
                                              .exhibition(exhibitionEntity)
                                              .visitedDate("2020-05-05")
                                              .isDeleted(false)
                                              .build();

        ReviewEntity editReviewEntity = ReviewEntity.builder()
                                                    .id(testReview.getId())
                                                    .title(editReviewRequest.getNewTitle())
                                                    .content(editReviewRequest.getNewContent())
                                                    .star(editReviewRequest.getNewStar())
                                                    .user(testReview.getUser())
                                                    .exhibition(testReview.getExhibition())
                                                    .visitedDate(editReviewRequest.getNewVisitedDate())
                                                    .isDeleted(false)
                                                    .build();

        ReviewDto editedReviewDto = ReviewDto.toDto(editReviewEntity);

        when(reviewService.editReview(any(), any(), any())).thenReturn(editedReviewDto);

        mockMvc.perform(put("/api/v1/reviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(editReviewRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.id").exists())
               .andExpect(jsonPath("$.result.title").exists())
               .andExpect(jsonPath("$.result.content").exists())
               .andExpect(jsonPath("$.result.star").exists())
               .andExpect(jsonPath("$.result.userName").exists())
               .andExpect(jsonPath("$.result.exhibitionName").exists())
               .andExpect(jsonPath("$.result.visitedDate").exists())
               .andDo(print());

        // verify(reviewService).editReview(testUser.getEmail(), editReviewRequest, testReview.getId());
    }

    @Test
    @WithMockUser
    @DisplayName("리뷰 수정 실패 - 리뷰 작성자와 수정자가 불일치")
    void edit_review_fail1() throws Exception {

        //테스트용 유저
        UserEntity reviewer = UserEntityFixture.getUser("reviewer@withmuma.com", "password");
        UserEntity testUser = UserEntityFixture.getUser("testUser@withmuma.com", "password");

        // 테스트용 리뷰
        ReviewEntity testReview = ReviewEntity.builder()
                                              .id(1L)
                                              .title("기존 리뷰")
                                              .content("기존 리뷰 내용")
                                              .star(5)
                                              .user(reviewer)
                                              .exhibition(exhibitionEntity)
                                              .visitedDate("2020-05-05")
                                              .isDeleted(false)
                                              .build();

        // 리뷰 수정 request
        EditReviewRequest editReviewRequest = EditReviewRequest.builder()
                                                               .newTitle("수정한 리뷰 제목")
                                                               .newContent("수정한 리뷰 내용")
                                                               .newStar(3)
                                                               .newVisitedDate("2022-05-05")
                                                               .build();

        ReviewEntity editReviewEntity = ReviewEntity.builder()
                                                    .id(testReview.getId())
                                                    .title(editReviewRequest.getNewTitle())
                                                    .content(editReviewRequest.getNewContent())
                                                    .star(editReviewRequest.getNewStar())
                                                    .user(testUser) //testUser
                                                    .exhibition(testReview.getExhibition())
                                                    .visitedDate(editReviewRequest.getNewVisitedDate())
                                                    .isDeleted(false)
                                                    .build();

        when(reviewService.editReview(any(), any(), any()))
            .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/reviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(editReviewRequest)))
               .andExpect(status().isUnauthorized())
               .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("리뷰 수정 실패 - DB Error")
    void edit_review_fail2() throws Exception {

        when(reviewService.editReview(any(), any(), any()))
            .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/reviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(editReviewRequest)))
               .andExpect(status().isInternalServerError())
               .andDo(print());
    }

    /* 조회 테스트 로직
        1. 행동 가정 (reviewService.getReview~)
        2. mockMvc.perform 수행 시 예측 결과가 나오는지 확인
         */

    @Test
    @DisplayName("리뷰 상세 조회 성공 - 인증된 사용자")
    @WithMockUser
    void get_review_success() throws Exception {

        ReviewEntity testReview = ReviewEntity.builder()
                                              .id(1L)
                                              .title("조회 테스트용 review title")
                                              .content("조회 테스트용 review content")
                                              .star(3)
                                              .user(UserEntityFixture.getUser("test@mail.com", "password"))
                                              .exhibition(exhibitionEntity)
                                              .isDeleted(false)
                                              .visitedDate("2023-02-17")
                                              .build();

        ReviewDto testDto = ReviewDto.toDto(testReview);

        GetReviewResponse getReviewResponse = GetReviewResponse.toResponse(testDto);

        when(reviewService.getReview(any()))
            .thenReturn(testDto);

        // 검증
        mockMvc.perform(get("/api/v1/reviews/1/details")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(getReviewResponse)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.id").exists())
               .andExpect(jsonPath("$.result.title").exists())
               .andExpect(jsonPath("$.result.content").exists())
               .andExpect(jsonPath("$.result.star").exists())
               .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("리뷰 상세 조회 실패 - 조회하려는 리뷰 없음")
    void get_review_fail1() throws Exception {

        when(reviewService.getReview(any()))
            .thenThrow(new AppException(ErrorCode.REVIEW_NOT_FOUND, ""));

        mockMvc.perform(get("/api/v1/reviews/1/details")
                            .with(csrf()))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Nested
    @DisplayName("리뷰 목록 조회")
    class GetAllReviews {

        @Test
        @WithMockUser
        @DisplayName("목록 조회 성공 - 여러 건 있는 경우 ")
        void get_review_list_success() throws Exception {

            Long review1 = 1L;
            Long review2 = 2L;
            Long review3 = 3L;

            ReviewEntity testReview = ReviewEntity.builder()
                                                  .id(review1)
                                                  .title("조회 테스트용 review title")
                                                  .content("조회 테스트용 review content")
                                                  .star(3)
                                                  .user(UserEntityFixture.getUser("test@mail.com", "password"))
                                                  .exhibition(exhibitionEntity)
                                                  .visitedDate("2023-02-17")
                                                  .isDeleted(false)
                                                  .build();
            ReviewEntity testReview2 = ReviewEntity.builder()
                                                   .id(review2)
                                                   .title("조회 테스트용 review title")
                                                   .content("조회 테스트용 review content")
                                                   .star(3)
                                                   .user(UserEntityFixture.getUser("test@mail.com", "password"))
                                                   .exhibition(exhibitionEntity)
                                                   .visitedDate("2023-02-17").isDeleted(false)
                                                   .build();
            ReviewEntity testReview3 = ReviewEntity.builder()
                                                   .id(review3)
                                                   .title("조회 테스트용 review title")
                                                   .content("조회 테스트용 review content")
                                                   .star(3)
                                                   .user(UserEntityFixture.getUser("test@mail.com", "password"))
                                                   .exhibition(exhibitionEntity)
                                                   .visitedDate("2023-02-17")
                                                   .isDeleted(false)
                                                   .build();

            Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "createdAt");

            List<ReviewDto> testReviews = new ArrayList<>();

            ReviewDto testReviewDto = ReviewDto.toDto(testReview);
            ReviewDto testReviewDto2 = ReviewDto.toDto(testReview2);
            ReviewDto testReviewDto3 = ReviewDto.toDto(testReview3);

            testReviews.add(testReviewDto);
            testReviews.add(testReviewDto2);
            testReviews.add(testReviewDto3);

            Page<ReviewDto> reviewDtoPage = new PageImpl<>(testReviews);

            ReviewPageResponse reviewPageResponse = new ReviewPageResponse(reviewDtoPage.getContent(), pageable);
            given(reviewService.getAllReviews(any(), any())).willReturn(reviewDtoPage);

            mockMvc.perform(get("/api/v1/reviews/" + exhibitionEntity.getId() + "/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(reviewPageResponse)))
                   .andExpect(status().isOk())
                   .andDo(print());

        }

        @Test
        @WithMockUser
        @DisplayName("목록 조회 성공 - 리뷰 없는 경우")
        void get_review_list_success2() throws Exception {

            given(reviewService.getAllReviews(any(), any())).willReturn(Page.empty());

            mockMvc.perform(get("/api/v1/reviews/" + exhibitionEntity.getId() + "/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                   //.content(objectMapper.writeValueAsBytes(reviewPageResponse)))
                   .andExpect(status().isOk())
                   .andDo(print());
        }
    }
}