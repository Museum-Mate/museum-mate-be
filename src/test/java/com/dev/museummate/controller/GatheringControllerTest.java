package com.dev.museummate.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.GatheringService;
import com.dev.museummate.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(GatheringController.class)
class GatheringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GatheringService gatheringService;

    @Test
    @DisplayName("모집 글 작성 - 성공")
    @WithMockUser
    void posts_success() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "한국", 3, "모집", "같이 갈 사람");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "한국", 5, "제목", "내용", Boolean.TRUE);
        //given
        given(gatheringService.posts(any(), any()))
            .willReturn(gatheringDto);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/posts")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(gatheringPostRequest)))
               .andDo(print())
               .andExpect(status().isOk());
        //then

    }

    @Test
    @DisplayName("모집 글 작성 - 실패#1 이메일 조회 불가")
    @WithMockUser
    void posts_fail_1() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "한국", 3, "모집", "같이 갈 사람");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "한국", 5, "제목", "내용", Boolean.TRUE);
        //given
        given(gatheringService.posts(any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/posts")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(gatheringPostRequest)))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then

    }

    @Test
    @DisplayName("모집 글 작성 - 실패#2 전시회 조회 불가")
    @WithMockUser
    void posts_fail_2() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "한국", 3, "모집", "같이 갈 사람");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "한국", 5, "제목", "내용", Boolean.TRUE);
        //given
        given(gatheringService.posts(any(), any()))
            .willThrow(new AppException(ErrorCode.EXHIBITION_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/posts")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(gatheringPostRequest)))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

}