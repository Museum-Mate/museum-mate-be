package com.dev.museummate.controller;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionEditRequest;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionWriteRequest;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExhibitionController.class)
class ExhibitionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ExhibitionService exhibitionService;

    @Autowired
    ObjectMapper objectMapper;

    private ExhibitionDto exhibitionDto1;

    @BeforeEach
    void set() {
        UserEntity user1 = UserEntity.builder()
                                     .id(1L)
                                     .email("test@test.com")
                                     .password("test")
                                     .name("김재근")
                                     .userName("geun")
                                     .birth("961210")
                                     .phoneNumber("010-9864-1772")
                                     .address("서울시 송파구")
                                     .role(UserRole.ROLE_USER)
                                     .build();

        exhibitionDto1 = ExhibitionDto.builder()
                                      .id(1L)
                                      .name("이집트미라전")
                                      .startAt("2022-12-01")
                                      .endAt("2022-12-31")
                                      .price("10000")
                                      .user(user1)
                                      .ageLimit("10")
                                      .statMale("")
                                      .statFemale("")
                                      .statAge10("")
                                      .statAge20("")
                                      .statAge30("")
                                      .statAge40("")
                                      .statAge50("")
                                      .detailInfo("test")
                                      .galleryLocation("test")
                                      .galleryName("test")
                                      .detailInfoImgUrl("")
                                      .mainImgUrl("")
                                      .noticeImgUrl("")
                                      .build();
    }

    @Nested
    @DisplayName("전시 상세 조회")
    class GetExhibition {

        @Test
        @WithMockUser
        @DisplayName("전시 상세 조회 성공")
        void getOne_Success() throws Exception {
            Long exhibitionId = 1L;

            ExhibitionDto exhibitionDto =
                ExhibitionDto.builder()
                             .id(exhibitionId)
                             .name("test")
                             .startAt("2022-12-01")
                             .endAt("2022-12-31")
                             .price("10000")
                             .ageLimit("10")
                             .detailInfo("test")
                             .galleryLocation("test")
                             .galleryName("test")
                             .build();

            given(exhibitionService.getOne(exhibitionId)).willReturn(exhibitionDto);

            mockMvc.perform(get("/api/v1/exhibitions/" + exhibitionId)
                                .with(csrf()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                   .andExpect(jsonPath("$.result.name").exists())
                   .andExpect(jsonPath("$.result.startAt").exists())
                   .andExpect(jsonPath("$.result.endAt").exists())
                   .andExpect(jsonPath("$.result.price").exists())
                   .andExpect(jsonPath("$.result.ageLimit").exists())
                   .andExpect(jsonPath("$.result.detailInfo").exists())
                   .andDo(print());
        }

        @Test
        @DisplayName("전시 상세 조회 실패 - 존재하지 않는 전시")
        @WithMockUser
        void getOne_Fail() throws Exception {

            Long notExistExhibitionId = 1L;
            given(exhibitionService.getOne(anyLong())).willThrow(
                new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "")
                                                                );

            mockMvc.perform(get("/api/v1/exhibitions/" + notExistExhibitionId)
                                .with(csrf()))
                   .andExpect(status().isNotFound())
                   .andDo(print());
        }
    }

    @Test
    @DisplayName("전시회 전체 리스트 조회 성공")
    @WithMockUser
    void exhibitionList_success() throws Exception {
//        500error
//        ExhibitionDto exhibitionDto = ExhibitionDto.builder().name("test").build();
//        Page<ExhibitionDto> exhibitionDtoPage = new PageImpl<>(List.of(exhibitionDto));
        given(exhibitionService.findAllExhibitions(any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/exhibitions").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsBytes(Page.empty())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.content").exists())
               .andExpect(jsonPath("$.result.pageable").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("전시 등록 성공")
    @WithMockUser
    void writeSuccess() throws Exception {

        UserEntity user1 = UserEntity.builder()
                                     .id(1L)
                                     .email("test@test.com")
                                     .password("test")
                                     .name("김재근")
                                     .userName("geun")
                                     .birth("961210")
                                     .phoneNumber("010-9864-1772")
                                     .address("서울시 송파구")
                                     .role(UserRole.ROLE_USER)
                                     .build();

        ExhibitionWriteRequest exhibitionWriteRequest = ExhibitionWriteRequest.builder()
                                                                              .name("test")
                                                                              .startAt("test")
                                                                              .endAt("test")
                                                                              .price("test")
                                                                              .ageLimit("test")
                                                                              .detailInfo("test")
                                                                              .galleryLocation("test")
                                                                              .galleryName("test")
                                                                              .mainImgUrl("test")
                                                                              .noticeImgUrl("test")
                                                                              .detailInfoImgUrl("test")
                                                                              .build();

        ExhibitionDto exhibitionDto = ExhibitionDto.builder()
                                                   .id(1L)
                                                   .name("test")
                                                   .startAt("test")
                                                   .endAt("test")
                                                   .price("test")
                                                   .ageLimit("test")
                                                   .detailInfo("test")
                                                   .galleryLocation("test")
                                                   .galleryName("test")
                                                   .user(user1)
                                                   .statMale("test")
                                                   .statFemale("test")
                                                   .statAge10("test")
                                                   .statAge20("test")
                                                   .statAge30("test")
                                                   .statAge40("test")
                                                   .statAge50("test")
                                                   .mainImgUrl("test")
                                                   .noticeImgUrl("test")
                                                   .detailInfoImgUrl("test")
                                                   .build();

        given(exhibitionService.write(any(), anyString())).willReturn(exhibitionDto);

        mockMvc.perform(post("/api/v1/exhibitions/new")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(exhibitionWriteRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.name").exists())
               .andExpect(jsonPath("$.result.startAt").exists())
               .andExpect(jsonPath("$.result.endAt").exists())
               .andExpect(jsonPath("$.result.price").exists())
               .andExpect(jsonPath("$.result.ageLimit").exists())
               .andExpect(jsonPath("$.result.detailInfo").exists())
               .andExpect(jsonPath("$.result.statMale").exists())
               .andExpect(jsonPath("$.result.statFemale").exists())
               .andExpect(jsonPath("$.result.statAge10").exists())
               .andExpect(jsonPath("$.result.statAge20").exists())
               .andExpect(jsonPath("$.result.statAge30").exists())
               .andExpect(jsonPath("$.result.statAge40").exists())
               .andExpect(jsonPath("$.result.statAge50").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("전시 등록 실패 - 인증 실패")
    void writeFailure() throws Exception {

        ExhibitionWriteRequest exhibitionWriteRequest = ExhibitionWriteRequest.builder()
                                                                              .name("test")
                                                                              .startAt("test")
                                                                              .endAt("test")
                                                                              .price("test")
                                                                              .ageLimit("test")
                                                                              .detailInfo("test")
                                                                              .galleryLocation("test")
                                                                              .galleryName("test")
                                                                              .mainImgUrl("test")
                                                                              .noticeImgUrl("test")
                                                                              .detailInfoImgUrl("test")
                                                                              .build();

        given(exhibitionService.write(any(), anyString())).willThrow(new AppException(ErrorCode.INVALID_TOKEN, ""));

        mockMvc.perform(post("/api/v1/exhibitions/new")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(exhibitionWriteRequest)))
               .andExpect(status().is3xxRedirection()) // 인증 실패로 인해서 Spring Security에서 리다이렉트 -> 302
               .andDo(print());

    }

    @Test
    @DisplayName("북마크 추가 성공")
    @WithMockUser
    void addToBookmarkSuccess() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkAddResponse("", 1l));
        mockMvc.perform(post("/api/v1/exhibitions/1/bookmarks")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("북마크 추가 실패 - 해당 전시 게시물이 없는 경우")
    @WithMockUser
    void addToBookmarkFailure1() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenThrow(new AppException(ErrorCode.NOT_FOUND_POST, ""));
        mockMvc.perform(post("/api/v1/exhibitions/1/bookmarks")
                            .with(csrf()))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Test
    @DisplayName("북마크 추가 실패 - 로그인 되지 않은 경우")
    @WithAnonymousUser
    void addToBookmarkFailure2() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkAddResponse("", 1l));
        mockMvc.perform(post("/api/v1/exhibitions/1/bookmarks")
                            .with(csrf()))
               .andExpect(status().is3xxRedirection()) // 인증 실패로 인해서 Spring Security에서 리다이렉트 -> 302
               .andDo(print());
    }

    @Test
    @DisplayName("전시회 정보 수정 성공")
    @WithMockUser
    public void edit_success() throws Exception {

        UserEntity user = UserEntity.builder()
                                    .id(1L)
                                    .email("test@test.com")
                                    .password("test")
                                    .name("김재근")
                                    .userName("geun")
                                    .birth("961210")
                                    .phoneNumber("010-9864-1772")
                                    .address("서울시 송파구")
                                    .role(UserRole.ROLE_USER)
                                    .build();

        ExhibitionEditRequest exhibitionEditRequest = ExhibitionEditRequest.builder()
                                                                           .id(1l)
                                                                           .name("이집트미라전")
                                                                           .startAt("09:00")
                                                                           .endAt("18:00")
                                                                           .price("18000")
                                                                           .ageLimit("8세")
                                                                           .detailInfo("none")
                                                                           .galleryLocation("서울")
                                                                           .galleryName("test")
                                                                           .user(user)
                                                                           .statMale("20%")
                                                                           .statFemale("80%")
                                                                           .mainImgUrl("test")
                                                                           .noticeImgUrl("test")
                                                                           .detailInfoImgUrl("test")
                                                                           .build();

        given(exhibitionService.edit(any(), any(), any())).willReturn(exhibitionDto1);

        mockMvc.perform(put("/api/v1/exhibitions/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(exhibitionEditRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.id").exists())
               .andExpect(jsonPath("$.result.name").exists())
               .andExpect(jsonPath("$.result.startAt").exists())
               .andExpect(jsonPath("$.result.endAt").exists())
               .andExpect(jsonPath("$.result.price").exists())
               .andExpect(jsonPath("$.result.ageLimit").exists())
               .andExpect(jsonPath("$.result.detailInfo").exists())
               .andExpect(jsonPath("$.result.galleryName").exists())
               .andExpect(jsonPath("$.result.statMale").exists())
               .andExpect(jsonPath("$.result.statFemale").exists())
               .andExpect(jsonPath("$.result.statAge10").exists())
               .andExpect(jsonPath("$.result.statAge20").exists())
               .andExpect(jsonPath("$.result.statAge30").exists())
               .andExpect(jsonPath("$.result.statAge40").exists())
               .andExpect(jsonPath("$.result.statAge50").exists())
               .andExpect(jsonPath("$.result.mainImgUrl").exists())
               .andExpect(jsonPath("$.result.noticeImgUrl").exists())
               .andExpect(jsonPath("$.result.detailInfoImgUrl").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("전시회 수정 실패 - DB Error")
    @WithMockUser
    void edit_fail_DB() throws Exception {

        UserEntity user = UserEntity.builder()
                                    .id(1l).email("www@www.com").password("1234").userName("moon")
                                    .birth("112233").phoneNumber("010-0000-0000").address("서울시").role(UserRole.ROLE_USER)
                                    .build();

        ExhibitionEditRequest exhibitionEditRequest = ExhibitionEditRequest.builder()
                                                                           .id(1l).name("이집트미라전").startAt("09:00").endAt("18:00")
                                                                           .price("18000").ageLimit("8세").detailInfo("none")
                                                                           .galleryLocation("서울").galleryName("test").user(user)
                                                                           .statMale("20%").statFemale("80%").mainImgUrl("www")
                                                                           .noticeImgUrl("www").detailInfoImgUrl("www")
                                                                           .build();

        given(exhibitionService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, "데이터베이스 에러"));

        mockMvc.perform(put("/api/v1/exhibitions/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(exhibitionEditRequest)))
               .andExpect(status().isInternalServerError())
               .andDo(print());
    }

    @Test
    @DisplayName("전시회 수정 실패 - 작성자 불일치")
    @WithMockUser
    void edit_fail_userName() throws Exception {

        UserEntity user = UserEntity.builder()
                                    .id(1l).email("www@www.com").password("1234").userName("moon")
                                    .birth("112233").phoneNumber("010-0000-0000").address("서울시").role(UserRole.ROLE_USER)
                                    .build();

        ExhibitionEditRequest exhibitionEditRequest = ExhibitionEditRequest.builder()
                                                                           .id(1l).name("이집트미라전").startAt("09:00").endAt("18:00")
                                                                           .price("18000").ageLimit("8세").detailInfo("none")
                                                                           .galleryLocation("서울").galleryName("Test").user(user)
                                                                           .statMale("20%").statFemale("80%").mainImgUrl("www")
                                                                           .noticeImgUrl("www").detailInfoImgUrl("www")
                                                                           .build();

        given(exhibitionService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 일치하지 않습니다."));

        mockMvc.perform(put("/api/v1/exhibitions/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(exhibitionEditRequest)))
               .andExpect(status().isUnauthorized())
               .andDo(print());
    }
}