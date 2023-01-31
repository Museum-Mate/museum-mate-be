package com.dev.museummate.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.GatheringService;
import com.dev.museummate.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/posts")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/posts")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/posts")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(gatheringPostRequest)))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("모집글 전체 리스트 조회 성공")
    @WithMockUser
    void gatheringList_success() throws Exception {

        given(gatheringService.findAllGatherings(any())).willReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/gatherings")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(Page.empty())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.content").exists())
               .andExpect(jsonPath("$.result.pageable").exists())
               .andDo(print());
    }

    @Nested
    @DisplayName("모집글 상세 조회")
    class GetGathering {
        @Test
        @WithMockUser
        @DisplayName("모집글 상세 조회 성공")
        void getOne_Success() throws Exception {
            Long gatheringId = 1L;

            UserEntity user1 = new UserEntity(1L, "test", "test", "test", "test", "test", "test", "test", UserRole.ROLE_USER);

            ExhibitionEntity exhibition = new ExhibitionEntity(1l, "name", "10:00", "18:00", "20000", "전체관람가", "temp",
                                                               "seoul",
                                                               new GalleryEntity(1l, "name", "address", "9", "18"), user1,
                                                               "temp", "temp", "temp", "temp", "temp", "temp",
                                                               "temp", "url", "url", "url");

            GatheringDto gatheringDto = GatheringDto.builder()
                                                    .id(gatheringId)
                                                    .meetDateTime("test")
                                                    .meetLocation("test")
                                                    .maxPeople(5)
                                                    .title("title")
                                                    .content("content")
                                                    .close(false)
                                                    .exhibition(exhibition)
                                                    .user(user1)
                                                    .createdAt(LocalDateTime.now())
                                                    .lastModifiedAt(LocalDateTime.of(2023, 02, 17, 10, 30))
                                                    .deletedAt(LocalDateTime.of(2023, 02, 17, 15, 10))
                                                    .createdBy("test")
                                                    .lastModifiedBy("test")
                                                    .build();

            given(gatheringService.getOne(gatheringId)).willReturn(gatheringDto);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/gatherings/" + gatheringId)
                                                  .with(csrf())
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                   .andExpect(jsonPath("$.result.id").exists())
                   .andExpect(jsonPath("$.result.meetDateTime").exists())
                   .andExpect(jsonPath("$.result.meetLocation").exists())
                   .andExpect(jsonPath("$.result.maxPeople").exists())
                   .andExpect(jsonPath("$.result.title").exists())
                   .andExpect(jsonPath("$.result.content").exists())
                   .andExpect(jsonPath("$.result.close").exists())
                   .andExpect(jsonPath("$.result.exhibitionId").exists())
                   .andExpect(jsonPath("$.result.userId").exists())
                   .andExpect(jsonPath("$.result.createdAt").exists())
                   .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                   .andExpect(jsonPath("$.result.deletedAt").exists())
                   .andExpect(jsonPath("$.result.createdBy").exists())
                   .andExpect(jsonPath("$.result.lastModifiedBy").exists())
                   .andDo(print());
        }

        @Test
        @DisplayName("모집글 상세 조회 실패 - 존재하지 않는 게시물")
        @WithMockUser
        void getOne_Fail() throws Exception {

            Long notExistGatheringId = 1L;

            given(gatheringService.getOne(anyLong())).willThrow(new AppException(ErrorCode.NOT_FOUND_POST, ""));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/gatherings/" + notExistGatheringId)
                                                  .with(csrf()))
                   .andExpect(status().isNotFound())
                   .andDo(print());
        }
    }

}