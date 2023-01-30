package com.dev.museummate.controller;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionWriteRequest;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Authenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

    Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "name");

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
                            .startsAt("2022-12-01")
                            .endsAt("2022-12-31")
                            .price("10000")
                            .ageLimit("10")
                            .detailInfo("test")
                            .galleryLocation("test")
                            .gallery(new GalleryEntity(1l,"name","address","9","18"))
                            .build();

            given(exhibitionService.getOne(exhibitionId)).willReturn(exhibitionDto);

            mockMvc.perform(get("/api/v1/exhibitions/" + exhibitionId)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.name").exists())
                    .andExpect(jsonPath("$.result.startsAt").exists())
                    .andExpect(jsonPath("$.result.endsAt").exists())
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
                    new AppException(ErrorCode.EXHIBITION_NOT_FOUND,"")
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
    void exhibitionList_success () throws Exception {
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

        GalleryEntity gallery = new GalleryEntity(1L, "name", "address", "9", "18");

        UserEntity user = new UserEntity(1L, "test", "test", "test", "test", "test", "test", "test", UserRole.ROLE_USER);

        ExhibitionWriteRequest exhibitionWriteRequest = new ExhibitionWriteRequest(
                "test", "test", "test", "test", "test", "test", "test",
                new GalleryEntity(1L, "name", "address", "9", "18"), "test", "test", "test");


        ExhibitionDto exhibitionDto = ExhibitionDto.builder()
                .id(1L)
                .name("test")
                .startsAt("test")
                .endsAt("test")
                .price("test")
                .ageLimit("test")
                .detailInfo("test")
                .galleryLocation("test")
                .gallery(gallery)
                .user(user)
                .statMale("test")
                .statFemale("test")
                .statAge10("test")
                .statAge20("test")
                .statAge30("test")
                .statAge40("test")
                .statAge50("test")
                .mainImgUrl("test")
                .noticeImgUrl("test")
                .detailImgUrl("test")
                .build();

        given(exhibitionService.write(any(), anyString())).willReturn(exhibitionDto);

        mockMvc.perform(post("/api/v1/exhibitions/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(exhibitionWriteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").exists())
                .andExpect(jsonPath("$.result.startsAt").exists())
                .andExpect(jsonPath("$.result.endsAt").exists())
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

        ExhibitionWriteRequest exhibitionWriteRequest = new ExhibitionWriteRequest(
                "test", "test", "test", "test", "test", "test", "test",
                new GalleryEntity(1L, "name", "address", "9", "18"), "test", "test", "test");

        given(exhibitionService.write(any(), anyString())).willThrow(new AppException(ErrorCode.INVALID_TOKEN, ""));

        mockMvc.perform(post("/api/v1/exhibitions/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(exhibitionWriteRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("북마크 추가 성공")
    @WithMockUser
    void addToBookmarkSuccess() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkAddResponse("",1l));
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
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkAddResponse("",1l));
        mockMvc.perform(post("/api/v1/exhibitions/1/bookmarks")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}