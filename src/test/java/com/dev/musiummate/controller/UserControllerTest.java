package com.dev.musiummate.controller;

import com.dev.musiummate.domain.dto.user.UserJoinRequest;
import com.dev.musiummate.domain.dto.user.UserJoinResponse;
import com.dev.musiummate.exception.AppException;
import com.dev.musiummate.exception.ErrorCode;
import com.dev.musiummate.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("회원가입 - 성공")
    @WithMockUser
    void join_success() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();
        UserJoinResponse userJoinResponse = new UserJoinResponse(userJoinRequest.getUserName());

        when(userService.join(any()))
                .thenReturn(userJoinResponse);

        mockMvc.perform(post("/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - 실패 #1 유저이름 중복")
    @WithMockUser
    void join_fail_1() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATE_USERNAME,"중복된 userName"));

        mockMvc.perform(post("/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 - 실패 #2 이메일 중복")
    @WithMockUser
    void join_fail_2() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATE_EMAIL,"중복된 이메일"));

        mockMvc.perform(post("/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }


}