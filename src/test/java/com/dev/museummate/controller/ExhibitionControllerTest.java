package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.service.ExhibitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
    void getOneSuccess() throws Exception{
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

}