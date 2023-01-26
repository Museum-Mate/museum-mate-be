package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.ExhibitionService;
import com.dev.museummate.service.MyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyController.class)
public class MyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MyService myService;

    private ExhibitionDto exhibitionDto;

    @BeforeEach
    void setUp(){
        exhibitionDto = ExhibitionDto.builder()
                .id(1l)
                .name("test")
                .startsAt("test")
                .endsAt("test")
                .ageLimit("test")
                .detailInfo("test")
                .galleryDetail("test")
                .gallery(new GalleryEntity(1l,"test","test","test","test")).build();

    }
    @Test
    @DisplayName("마이 캘린더 조회 성공")
    @WithMockUser
    void myCalendarSuccess() throws Exception {

        List<ExhibitionDto> exhibitionDtos = new ArrayList<>();
        exhibitionDtos.add(exhibitionDto);

        when(myService.getMyCalendar(any())).thenReturn(exhibitionDtos);

        mockMvc.perform(get("/api/v1/my/calendars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("마이 캘린더 조회 실패 - 유저가 존재하지 않는 경우")
    @WithMockUser
    void myCalendarFail() throws Exception {

        when(myService.getMyCalendar(any())).thenThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND,""));

        mockMvc.perform(get("/api/v1/my/calendars"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
