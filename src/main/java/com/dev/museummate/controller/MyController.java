package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.alarm.AlarmResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/alarms")
    public Response getAlarms(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication){
        Page<AlarmDto> alarmDtos = myService.getAlarms(pageable, authentication.getName());

        Page<AlarmResponse> alarmResponses = alarmDtos.map(alarmDto -> AlarmResponse.builder()
                .userName(alarmDto.getUser().getUserName())
                .exhibitionName(alarmDto.getExhibition().getName())
                .alarmMessage(alarmDto.getAlarmMessage()).build());
        return Response.success(alarmResponses);
    }
}
