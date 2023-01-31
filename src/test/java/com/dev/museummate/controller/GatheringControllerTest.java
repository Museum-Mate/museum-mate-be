package com.dev.museummate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.GatheringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    @Test
    @DisplayName("참가 신청 - 성공")
    @WithMockUser
    void enroll_success() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willReturn("신청이 완료 되었습니다.");

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("참가 신청 - 실패#1 이메일 조회 불가")
    @WithMockUser
    void enroll_fail_1() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("참가 신청 - 실패#2 모집 글 조회 불가")
    @WithMockUser
    void enroll_fail_2() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("참가 신청 - 실패#3 이미 신청한 회원이 중복 신청할 경우")
    @WithMockUser
    void enroll_fail_3() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.DUPLICATED_ENROLL, "중복된 신청입니다."));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gathering/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isConflict());
        //then
    }

    @Test
    @DisplayName("참가 신청 목록 조회 - 성공")
    @WithMockUser
    void enrollList_success() throws Exception {

        List<GatheringResponse> lger = new ArrayList<>();
        GatheringResponse ger = new GatheringResponse(1L,"username", Boolean.TRUE,LocalDateTime.now());
        lger.add(ger);

        given(gatheringService.enrollList(any(),any()))
            .willReturn(lger);

        //when
        mockMvc.perform(get("/api/v1/gathering/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("참가 신청 목록 조회 - 실패#1 이메일 조회 실패")
    @WithMockUser
    void enrollList_fail1() throws Exception {

        List<GatheringResponse> lger = new ArrayList<>();
        GatheringResponse ger = new GatheringResponse(1L,"username", Boolean.TRUE,LocalDateTime.now());
        lger.add(ger);

        given(gatheringService.enrollList(any(),any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND,""));

        //when
        mockMvc.perform(get("/api/v1/gathering/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("참가 신청 목록 조회 - 실패#2 모집 글 조회 실패")
    @WithMockUser
    void enrollList_fail2() throws Exception {

        List<GatheringResponse> lger = new ArrayList<>();
        GatheringResponse ger = new GatheringResponse(1L,"username", Boolean.TRUE,LocalDateTime.now());
        lger.add(ger);

        given(gatheringService.enrollList(any(),any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,""));

        //when
        mockMvc.perform(get("/api/v1/gathering/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }


}