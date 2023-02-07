package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.MyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private AlarmDto alarmDto;


    @BeforeEach
    void setUp(){
        exhibitionDto = ExhibitionDto.builder()
                                     .id(1l)
                                     .name("test")
                                     .startAt("test")
                                     .endAt("test")
                                     .ageLimit("test")
                                     .price("100원")
                                     .detailInfo("test")
                                     .galleryLocation("test")
                                     .galleryName("test")
                                     .build();

        alarmDto = AlarmDto.builder()
                .user(UserEntity.builder().userName("test").build())
                .exhibition(ExhibitionEntity.builder().name("test").build())
                .alarmMessage("")
                .build();
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

    @Test
    @DisplayName("알람 조회 성공")
    @WithMockUser
    void getAlarmsSuccess() throws Exception {

        Page<AlarmDto> dtoPage = new PageImpl<>(List.of(alarmDto));

        when(myService.getAlarms(any(), any())).thenReturn(dtoPage);

        mockMvc.perform(get("/api/v1/my/alarms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("알람 조회 실패 - 유저가 존재하지 않는 경우")
    @WithMockUser
    void getAlarmsFail() throws Exception {

        when(myService.getAlarms(any(), any())).thenThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND,""));

        mockMvc.perform(get("/api/v1/my/alarms"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
