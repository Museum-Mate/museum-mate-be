package com.dev.museummate.controller;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.exhibition.*;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.dialect.TiDBDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        exhibitionDto1 = new ExhibitionDto(1l, "예술의전당", "09:00", "18:00", "18000", "8세", "none",
                "서울", new GalleryEntity(1l, "예술의전당", "서울시", "09:00", "19:00"),
                new UserEntity(1l, "www@www.com", "1234", "moon", "112233", "010-0000-0000", "서울시", UserRole.ROLE_EDITOR),
                "20%", "80%", "20%", "20%", "20%", "20%", "20%",
                "www", "www", "www");

//        GalleryEntity gallery = new GalleryEntity(1l, "moon", "서울", "09:00", "18:00");
//        UserEntity user = new UserEntity(1l, "www@www.com", "1111", "moon", "000000", "01000000000", "서울시", UserRole.ROLE_EDITOR);
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
                            .startsAt("2022-12-01")
                            .endsAt("2022-12-31")
                            .price("10000")
                            .ageLimit("10")
                            .detailInfo("test")
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
                    .andExpect(jsonPath("$.result.galleryDetail").exists())
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

    @Test
    @DisplayName("전시회 정보 수정 성공")
    @WithMockUser
    public void edit_success() throws Exception {

        ExhibitionEditRequest exhibitionEditRequest = new ExhibitionEditRequest(1l, "소마미술관", "09:00", "18:00", "18000", "8세", "none",
                "서울", new GalleryEntity(1l, "예술의전당", "서울시", "09:00", "19:00"),
                new UserEntity(1l, "www@www.com", "1234", "moon", "112233", "010-0000-0000", "서울시", UserRole.ROLE_EDITOR),
                "20%", "80%", "20%", "20%", "20%", "20%", "20%",
                "www", "www", "www");

        given(exhibitionService.edit(any(), any(), any())).willReturn(exhibitionDto1);

        mockMvc.perform(put("/api/v1/exhibitions/1/edit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(exhibitionEditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
//                .andExpect(jsonPath("$.result.exhibitionId").exists())
                .andDo(print());
    }
}