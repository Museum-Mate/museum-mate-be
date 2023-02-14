package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.Response;
import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.alarm.AlarmResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.domain.dto.review.GetReviewResponse;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.user.UserDto;
import com.dev.museummate.service.MyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my")
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    @GetMapping()
    public Response getInfo(Authentication authentication) {
        UserDto userDto = myService.getMyInfo(authentication.getName());
        return Response.success(userDto);
    }

    @GetMapping("/calendars")
    public Response getMyCalendar(Authentication authentication){
        List<ExhibitionDto> exhibitionDtos = myService.getMyCalendar(authentication.getName());
        return Response.success(exhibitionDtos.stream().map(exhibition ->
                ExhibitionResponse.of(exhibition)));
    }

    @GetMapping("/alarms")
    public Response getAlarms(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication){
        Page<AlarmDto> alarmDtos = myService.getAlarms(pageable, authentication.getName());

        Page<AlarmResponse> alarmResponses = alarmDtos.map(alarmDto -> AlarmResponse.builder()
                .userName(alarmDto.getUser().getUserName())
                .exhibitionName(alarmDto.getExhibition().getName())
                .alarmMessage(alarmDto.getAlarmMessage()).build());
        return Response.success(alarmResponses);
    }

    @GetMapping("/reviews")
    public Response getReviews(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication){
        Page<ReviewDto> reviewDtos = myService.getMyReviews(pageable, authentication.getName());

        Page<GetReviewResponse> getReviewResponses = reviewDtos.map(reviewDto -> GetReviewResponse.toResponse(reviewDto));

        return Response.success(getReviewResponses);
    }

    @GetMapping("/gatherings")
    public Response getGatherings(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication){
        Page<GatheringDto> gatheringDtos = myService.getMyGatherings(pageable, authentication.getName());

        Page<GatheringResponse> gatheringResponses = gatheringDtos.map(gatheringDto -> GatheringResponse.createGetOne(gatheringDto));

        return Response.success(gatheringResponses);
    }

    @GetMapping("/gatherings/enrolls")
    public Response getEnrolls(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         Authentication authentication){
        Page<GatheringDto> enrollDtos = myService.getMyEnrolls(pageable, authentication.getName());

        Page<GatheringResponse> gatheringResponses = enrollDtos.map(gatheringDto -> GatheringResponse.createGetOne(gatheringDto));

        return Response.success(gatheringResponses);
    }

    @GetMapping("/gatherings/approves")
    public Response getApproves(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                               Authentication authentication){
        Page<GatheringDto> enrollDtos = myService.getMyApprove(pageable, authentication.getName());

        Page<GatheringResponse> gatheringResponses = enrollDtos.map(gatheringDto -> GatheringResponse.createGetOne(gatheringDto));

        return Response.success(gatheringResponses);
    }

}
