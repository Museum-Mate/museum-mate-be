package com.dev.museummate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.gathering.CommentDto;
import com.dev.museummate.domain.dto.gathering.CommentRequest;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringParticipantResponse;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.domain.dto.gathering.ParticipantDto;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.GatheringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
    @DisplayName("?????? ??? ?????? - ??????")
    @WithMockUser
    void posts_success() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "??????", 3, "??????", "?????? ??? ??????");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "??????", 5, "??????", "??????", Boolean.TRUE);
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
    @DisplayName("?????? ??? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void posts_fail_1() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "??????", 3, "??????", "?????? ??? ??????");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "??????", 5, "??????", "??????", Boolean.TRUE);
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
    @DisplayName("?????? ??? ?????? - ??????#2 ????????? ?????? ??????")
    @WithMockUser
    void posts_fail_2() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "??????", 3, "??????", "?????? ??? ??????");

        GatheringDto gatheringDto = new GatheringDto(1L, "23/10/29", "??????", 5, "??????", "??????", Boolean.TRUE);
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
    @DisplayName("????????? ?????? ??????")
    @WithMockUser
    void edit_Success() throws Exception {

        Long gatheringId = 1L;

        GatheringDto gatheringDto = new GatheringDto(1L, "test", "test", 5, "test", "test", false);

        GatheringPostRequest request = new GatheringPostRequest(gatheringId, "test1", "test", 4, "test", "test");

        given(gatheringService.edit(any(), any(), any())).willReturn(gatheringDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/gatherings/1")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.gatheringId").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????? ?????????")
    @WithMockUser
    void edit_Fail1() throws Exception {

        Long gatheringId = 1L;

        GatheringPostRequest request = new GatheringPostRequest(gatheringId, "test1", "test", 4, "test", "test");

        given(gatheringService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.FORBIDDEN_ACCESS, ""));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/gatherings/1")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(request)))
               .andExpect(status().isForbidden())
               .andDo(print());

    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????????")
    @WithAnonymousUser
    void edit_Fail2() throws Exception {

        Long gatheringId = 1L;

        GatheringPostRequest request = new GatheringPostRequest(gatheringId, "test1", "test", 4, "test", "test");

        given(gatheringService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/gatherings/1")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(request)))
               .andExpect(status().is3xxRedirection()) // ?????? ????????? ??????????????? -> Location:"http://localhost/login"
               .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithMockUser
    void delete_Success() throws Exception {

        Long gatheringId = 1L;

        given(gatheringService.delete(any(), any())).willReturn(gatheringId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/gatherings/" + gatheringId)
                                              .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.gatheringId").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????? ?????????")
    @WithMockUser
    void delete_Fail1() throws Exception {

        given(gatheringService.delete(any(), any())).willThrow(new AppException(ErrorCode.FORBIDDEN_ACCESS, ""));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/gatherings/1")
                                              .with(csrf()))
               .andExpect(status().isForbidden())
               .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ?????? ??????")
    @WithMockUser
    void delete_Fail2() throws Exception {

        given(gatheringService.delete(any(), any())).willThrow(new AppException(ErrorCode.INVALID_REQUEST, ""));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/gatherings/1")
                                              .with(csrf()))
               .andExpect(status().isUnauthorized())
               .andDo(print());
    }

    @Test
    @DisplayName("?????? ??? ?????? - ??????#1 ?????? ?????? ???????????? ?????? ?????? ?????? ??? ??????")
    @WithMockUser
    void edit_fail() throws Exception {

        GatheringPostRequest gatheringPostRequest = new GatheringPostRequest(1L, "23/10/29", "??????", 3, "??????", "?????? ??? ??????");
        //given
        given(gatheringService.edit(any(), any(),any()))
            .willThrow(new AppException(ErrorCode.CONFLICT, "conflict ??????"));

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/gatherings/1")
                                              .with(csrf())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsBytes(gatheringPostRequest)))
               .andDo(print())
               .andExpect(status().isConflict());
        //then
    }


    @Test
    @DisplayName("?????? ?????? - ??????")
    @WithMockUser
    void enroll_success() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willReturn("????????? ?????? ???????????????.");

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void enroll_fail_1() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#2 ?????? ??? ?????? ??????")
    @WithMockUser
    void enroll_fail_2() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, ""));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#3 ?????? ????????? ????????? ?????? ????????? ??????")
    @WithMockUser
    void enroll_fail_3() throws Exception {

        given(gatheringService.enroll(any(), any()))
            .willThrow(new AppException(ErrorCode.DUPLICATED_ENROLL, "????????? ???????????????."));

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/gatherings/1/enroll")
                                              .with(csrf()))
               .andDo(print())
               .andExpect(status().isConflict());
        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????")
    @WithMockUser
    void enrollList_success() throws Exception {

        List<ParticipantDto> lger = new ArrayList<>();

        given(gatheringService.enrollList(any(), any()))
            .willReturn(lger);

        //when
        mockMvc.perform(get("/api/v1/gatherings/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());

        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void enrollList_fail1() throws Exception {

        List<GatheringParticipantResponse> lger = new ArrayList<>();
        GatheringParticipantResponse ger = new GatheringParticipantResponse(1L,
                                                                            "username",
                                                                            Boolean.TRUE,
                                                                            LocalDateTime.now());
        lger.add(ger);

        given(gatheringService.enrollList(any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        //when
        mockMvc.perform(get("/api/v1/gatherings/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????#2 ?????? ??? ?????? ??????")
    @WithMockUser
    void enrollList_fail2() throws Exception {

        List<GatheringParticipantResponse> lger = new ArrayList<>();
        GatheringParticipantResponse ger = new GatheringParticipantResponse(1L,
                                                                            "username",
                                                                            Boolean.TRUE,
                                                                            LocalDateTime.now());
        lger.add(ger);

        given(gatheringService.enrollList(any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, ""));

        //when
        mockMvc.perform(get("/api/v1/gatherings/1/enroll/list")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    @WithMockUser
    void approve_success() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willReturn("????????? ???????????????.");

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void approve_fail_1() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#2 ????????? ?????? ??????")
    @WithMockUser
    void approve_fail_2() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, ""));

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#3 ?????? ??? ???????????? ?????? ????????? ?????? ??????")
    @WithMockUser
    void approve_fail_3() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.INVALID_REQUEST, ""));

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#4 ?????? ????????? ?????? ??????")
    @WithMockUser
    void approve_fail_4() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.PARTICIPANT_NOT_FOUND, ""));

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#5 ?????? ???????????? ?????? ??? ??? ??? ?????? ????????? ??????")
    @WithMockUser
    void approve_fail_5() throws Exception {

        given(gatheringService.approve(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.FORBIDDEN_ACCESS, ""));

        mockMvc.perform(get("/api/v1/gatherings/1/enroll/1")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    @WithMockUser
    void enroll_cancel_success() throws Exception {

        given(gatheringService.cancel(any(), any()))
            .willReturn("?????? ?????? ??????");

        mockMvc.perform(delete("/api/v1/gatherings/1/cancel")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void enroll_cancel_fail_1() throws Exception {

        given(gatheringService.cancel(any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, ""));

        mockMvc.perform(delete("/api/v1/gatherings/1/cancel")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#2 ?????? ?????? ??????")
    @WithMockUser
    void enroll_cancel_fail_2() throws Exception {

        given(gatheringService.cancel(any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, ""));

        mockMvc.perform(delete("/api/v1/gatherings/1/cancel")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#3 ?????? ?????? ??????")
    @WithMockUser
    void enroll_cancel_fail_3() throws Exception {

        given(gatheringService.cancel(any(), any()))
            .willThrow(new AppException(ErrorCode.PARTICIPANT_NOT_FOUND, ""));

        mockMvc.perform(delete("/api/v1/gatherings/1/cancel")
                            .with(csrf()))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? ??????")
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
    @DisplayName("????????? ?????? ??????")
    class GetGathering {

        @Test
        @WithMockUser
        @DisplayName("????????? ?????? ?????? ??????")
        void getOne_Success() throws Exception {
            Long gatheringId = 1L;
//            Long id, java.lang.String email, java.lang.String password, java.lang.String name, java.lang.String userName, java.lang.String birth, java.lang.String phoneNumber,
//            java.lang.String address,
//            UserRole role, java.lang.String providerId, String providerType

            UserEntity user1 = UserEntity.builder()
                                         .id(1L)
                                         .email("test@test.com")
                                         .password("test")
                                         .name("?????????")
                                         .userName("geun")
                                         .birth("961210")
                                         .phoneNumber("010-9864-1772")
                                         .address("????????? ?????????")
                                         .role(UserRole.ROLE_USER)
                                         .build();

            ExhibitionEntity exhibition = ExhibitionEntity.builder()
                                                           .id(1L)
                                                           .name("name")
                                                           .startAt("10:00")
                                                           .endAt("18:00")
                                                           .price("20000")
                                                           .ageLimit("???????????????")
                                                           .detailInfo("temp")
                                                           .galleryLocation("seoul")
                                                           .galleryName("test")
                                                           .user(user1)
                                                           .statMale("temp").
                                                           statFemale("temp")
                                                           .statAge_10("temp")
                                                           .statAge_20("temp")
                                                           .statAge_30("temp")
                                                           .statAge_40("temp")
                                                           .statAge_50("temp").
                                                           mainImgUrl("temp").
                                                           noticeImgUrl("temp")
                                                           .detailInfoImgUrl("temp").build();

            GatheringDto gatheringDto = GatheringDto.builder()
                                                    .id(gatheringId)
                                                    .meetDateTime("test")
                                                    .meetLocation("test")
                                                    .currentPeople(3)
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

            GatheringResponse gatheringResponse = GatheringResponse.builder()
                                                                   .id(gatheringId)
                                                                   .meetDateTime(gatheringDto.getMeetDateTime())
                                                                   .meetLocation(gatheringDto.getMeetLocation())
                                                                   .currentPeople(gatheringDto.getCurrentPeople())
                                                                   .maxPeople(gatheringDto.getMaxPeople())
                                                                   .title(gatheringDto.getTitle())
                                                                   .content(gatheringDto.getContent())
                                                                   .close(gatheringDto.getClose())
                                                                   .exhibitionName(gatheringDto.getExhibition().getName())
                                                                   .exhibitionMainUrl(gatheringDto.getExhibition().getMainImgUrl())
                                                                   .userName(gatheringDto.getUser().getUserName())
                                                                   .createdAt(gatheringDto.getCreatedAt())
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
                   .andExpect(jsonPath("$.result.currentPeople").exists())
                   .andExpect(jsonPath("$.result.maxPeople").exists())
                   .andExpect(jsonPath("$.result.title").exists())
                   .andExpect(jsonPath("$.result.content").exists())
                   .andExpect(jsonPath("$.result.close").exists())
                   .andExpect(jsonPath("$.result.exhibitionName").exists())
                   .andExpect(jsonPath("$.result.exhibitionMainUrl").exists())
                   .andExpect(jsonPath("$.result.userName").exists())
                   .andExpect(jsonPath("$.result.createdAt").exists())
                   .andDo(print());
        }

        @Test
        @DisplayName("????????? ?????? ?????? ?????? - ???????????? ?????? ?????????")
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


    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    @WithMockUser
    void write_comment_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.")
                                          .build();

        //given
        given(gatheringService.writeComment(any(), any(), any()))
            .willReturn(commentDto);

        //when
        mockMvc.perform(post("/api/v1/gatherings/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.gatheringId").exists())
               .andExpect(jsonPath("$.result.content").exists());
        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#1 ?????? ??? ?????? ??????")
    @WithMockUser
    void write_comment_fail_1() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.")
                                          .build();

        //given
        given(gatheringService.writeComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,"?????? ??????"));

        //when
        mockMvc.perform(post("/api/v1/gatherings/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????#2 ????????? ?????? ??????")
    @WithMockUser
    void write_comment_fail_2() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.")
                                          .build();

        //given
        given(gatheringService.writeComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND,"????????? ?????? ??? ????????????."));

        //when
        mockMvc.perform(post("/api/v1/gatherings/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????")
    @WithMockUser
    void comment_list_success() throws Exception {

        //given
        given(gatheringService.getComments(any(), any()))
            .willReturn(Page.empty());

        //when
        mockMvc.perform(get("/api/v1/gatherings/1/comments")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.content").exists())
               .andExpect(jsonPath("$.result.pageable").exists());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#1 ?????? ??? ?????? ??????")
    @WithMockUser
    void comment_list_fail_1() throws Exception {

        //given
        given(gatheringService.getComments(any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, "?????? ??? ?????? ??????"));

        //when
        mockMvc.perform(get("/api/v1/gatherings/1/comments")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????")
    @WithMockUser
    void comment_modify_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.modifyComment(any(), any(),any(),any()))
            .willReturn(commentDto);

        //when
        mockMvc.perform(put("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.gatheringId").exists())
               .andExpect(jsonPath("$.result.content").exists());

        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#1 ?????? ??? ?????? ??????")
    @WithMockUser
    void comment_modify_fail_1() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.modifyComment(any(), any(), any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, "?????? ??? ?????? ??????"));

        //when
        mockMvc.perform(put("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#2 ????????? ?????? ??????")
    @WithMockUser
    void comment_modify_fail_2() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.modifyComment(any(), any(), any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, "????????? ?????? ??????"));

        //when
        mockMvc.perform(put("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#3 ?????? ?????? - ?????? ?????? ?????????")
    @WithMockUser
    void comment_modify_fail_3() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.modifyComment(any(), any(), any(), any()))
            .willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "???????????? ?????? ???????????????."));

        //when
        mockMvc.perform(put("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#4 ?????? ?????? ??????")
    @WithMockUser
    void comment_modify_fail_4() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.modifyComment(any(), any(), any(), any()))
            .willThrow(new AppException(ErrorCode.CONTENT_NOT_FOUND, "????????? ????????? ??? ????????????."));

        //when
        mockMvc.perform(put("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????")
    @WithMockUser
    void comment_delete_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.deleteComment(any(), any(), any()))
            .willReturn("?????? ?????? ??????");

        //when
        mockMvc.perform(delete("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#1 ????????? ?????? ??????")
    @WithMockUser
    void comment_delete_fail1() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.deleteComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, "????????? ?????? ??????"));

        //when
        mockMvc.perform(delete("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#2 ?????? ??? ?????? ??????")
    @WithMockUser
    void comment_delete_fail2() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.deleteComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND, "????????? ?????? ??????"));

        //when
        mockMvc.perform(delete("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#3 ?????? ?????? - ?????? ?????? ?????????")
    @WithMockUser
    void comment_delete_fail3() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.deleteComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "???????????? ?????? ???????????????."));

        //when
        mockMvc.perform(delete("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("?????? ?????? - ??????#4 ?????? ?????? - ?????? ?????? ??????")
    @WithMockUser
    void comment_delete_fail4() throws Exception {

        CommentRequest commentRequest = new CommentRequest("???????????????.- ??????");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .content("???????????????.- ??????")
                                          .build();

        //given
        given(gatheringService.deleteComment(any(), any(), any()))
            .willThrow(new AppException(ErrorCode.CONTENT_NOT_FOUND, "????????? ?????? ??? ????????????."));

        //when
        mockMvc.perform(delete("/api/v1/gatherings/1/comments/1")
                            .with(csrf())
               )
               .andDo(print())
               .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    @WithMockUser
    void write_reply_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .parentId(2L)
                                          .content("???????????????.")
                                          .build();

        //given
        given(gatheringService.writeReply(any(), any(), any(),any()))
            .willReturn(commentDto);

        //when
        mockMvc.perform(post("/api/v1/gatherings/1/comments/1/reply")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentRequest))
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.gatheringId").exists())
               .andExpect(jsonPath("$.result.content").exists());
        //then
    }


}