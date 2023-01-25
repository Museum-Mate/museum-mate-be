package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Nested
    @DisplayName("전시 상세 조회")
    class GetExhibition {
        @Test
        @WithMockUser
        @DisplayName("전시 상세 조회 성공")
        void getOne_Success() throws Exception {
            Long exhibitionId = 1L;

            ExhibitionResponse exhibitionResponse =
                    ExhibitionResponse.builder()
                            .galleryId(exhibitionId)
                            .name("test")
                            .startsAt("2022-12-01")
                            .endsAt("2022-12-31")
                            .price("10000")
                            .ageLimit("10")
                            .detailInfo("test")
                            .galleryDetail("test")
                            .build();

            given(exhibitionService.getOne(exhibitionId)).willReturn(exhibitionResponse);

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
                    .andExpect(jsonPath("$.result.galleryDetail").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("전시 상세 조회 실패 - 존재하지 않는 전시")
        void getOne_Fail() throws Exception {

            Long existExhibitionId = 1L;
            Long notExistExhibitionId = 2L;

            ExhibitionResponse exhibitionResponse =
                    ExhibitionResponse.builder()
                            .galleryId(1L)
                            .name("test")
                            .startsAt("2022-12-01")
                            .endsAt("2022-12-31")
                            .price("10000")
                            .ageLimit("10")
                            .detailInfo("test")
                            .galleryDetail("test")
                            .build();

            given(exhibitionService.getOne(1L)).willReturn(exhibitionResponse);

            mockMvc.perform(get("/api/v1/exhibitions/" + notExistExhibitionId)
                            .with(csrf()))
                    .andExpect(status().is4xxClientError())
                    .andDo(print());
        }
    }
    
    @Test
    @DisplayName("전시 상세 조회 성공")
    @WithMockUser
    void getOneSuccess() throws Exception {
        long exhibitionId = 1l;

        given(exhibitionService.getOne(exhibitionId)).willReturn(exhibitionResponse1);

        mockMvc.perform(get("/exhibiitons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.name").exists())
                .andExpect(jsonPath("$.result.startsAt").exists())
                .andExpect(jsonPath("$.result.endsAt").exists())
                .andExpect(jsonPath("$.result.price").exists())
                .andExpect(jsonPath("$.result.ageLimit").exists())
                .andExpect(jsonPath("$.result.detailInfo").exists())
                .andExpect(jsonPath("$.result.galleryDetail").exists())
                .andDo(print());

    }
    
    
    @Test
    @DisplayName("전시회 전체 리스트 조회 성공")
    void exhibitionList_success () throws Exception {

        mockMvc.perform(get("api/v1/exhibitions")
                        .param("size", "20")
                        .param("sort", "name, DESC"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(exhibitionService).findAllExhibitions(pageableArgumentCaptor.capture());
        PageRequest pageRequest = (PageRequest) pageableArgumentCaptor.getAllValues();

        assertEquals(20, pageRequest.getPageSize());
        assertEquals(Sort.by("name", "DESC"), pageRequest.withSort(Sort.by("name", "DESC")).getSort());
    }

    @Test
    @DisplayName("북마크 추가 성공")
    @WithMockUser
    void addToBookmarkSuccess() throws Exception {
        doNothing().when(exhibitionService).addToBookmark(any(), any());
        mockMvc.perform(post("/exhibition/1/bookmark")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("북마크 추가 실패 - 해당 전시 게시물이 없는 경우")
    @WithMockUser
    void addToBookmarkFailure1() throws Exception {
        doThrow(new AppException(ErrorCode.NOT_FOUND_POST, "")).when(exhibitionService).addToBookmark(any(), any());
        mockMvc.perform(post("/exhibition/1/bookmark")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("북마크 추가 실패 - 로그인 되지 않은 경우")
    @WithAnonymousUser
    void addToBookmarkFailure2() throws Exception {
        doNothing().when(exhibitionService).addToBookmark(any(), any());
        mockMvc.perform(post("/exhibition/1/bookmark")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}



