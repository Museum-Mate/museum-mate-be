package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.bookmark.BookmarkResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

    private ExhibitionResponse exhibitionResponse1;

    // 아직 데이터가 없어서 나중에 채울 예정
    @BeforeEach
    void setup() {
        exhibitionResponse1 = new ExhibitionResponse("a", "a", "a", "a", "a", "a", "a",1l);
    }

    @Test
    @DisplayName("전시 상세 조회 성공")
    @WithMockUser
    void getOneSuccess() throws Exception {
        long exhibitionId = 1l;

        given(exhibitionService.getOne(exhibitionId)).willReturn(exhibitionResponse1);

        mockMvc.perform(get("/exhibitions/1"))
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
    @DisplayName("북마크 추가 성공")
    @WithMockUser
    void addToBookmarkSuccess() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkResponse(""));
        mockMvc.perform(post("/exhibitions/1/bookmarks")
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
        mockMvc.perform(post("/exhibitions/1/bookmarks")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("북마크 추가 실패 - 로그인 되지 않은 경우")
    @WithAnonymousUser
    void addToBookmarkFailure2() throws Exception {
        when(exhibitionService.addToBookmark(anyLong(), anyString())).thenReturn(new BookmarkResponse(""));
        mockMvc.perform(post("/exhibitions/1/bookmarks")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}