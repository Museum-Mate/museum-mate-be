package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/my")
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    @GetMapping("/calendars")
    public Response getMyCalendar(Authentication authentication){
        List<ExhibitionDto> exhibitionDtos = myService.getMyCalendar(authentication.getName());
        return Response.success(exhibitionDtos.stream().map(exhibition ->
                ExhibitionResponse.of(exhibition)));
    }
}
