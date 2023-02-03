package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringParticipantResponse;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringPostResponse;
import com.dev.museummate.domain.dto.gathering.ParticipantDto;
import com.dev.museummate.service.GatheringService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gatherings")
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/posts")
    public Response<GatheringPostResponse> posts(@RequestBody GatheringPostRequest gatheringPostRequest, Authentication authentication) {

        GatheringDto gatheringDto = gatheringService.posts(gatheringPostRequest,authentication.getName());
        return Response.success(new GatheringPostResponse(gatheringDto.getId()));
    }

    @PostMapping("/{gatheringId}/enroll")
    public Response<String> enroll(@PathVariable Long gatheringId,Authentication authentication) {
        String msg = gatheringService.enroll(gatheringId,authentication.getName());
        return Response.success(msg);
    }

    @GetMapping("/{gatheringId}/enroll/{participantId}")
    public Response<String> approve(@PathVariable Long gatheringId,@PathVariable Long participantId, Authentication authentication) {
        String msg = gatheringService.approve(gatheringId,participantId,authentication.getName());
        return Response.success(msg);
    }

    @GetMapping("/{gatheringId}/enroll/list")
    public Response<List<GatheringParticipantResponse>> enrollList(@PathVariable Long gatheringId, Authentication authentication) {
        List<ParticipantDto> participantDtos = gatheringService.enrollList(gatheringId, authentication.getName());
        List<GatheringParticipantResponse> gatheringParticipantResponses = participantDtos.stream()
                                                                               .map(ParticipantDto::toResponse)
                                                                               .collect(Collectors.toList());
        return Response.success(gatheringParticipantResponses);
    }

    @GetMapping("/{gatheringId}/approve/list")
    public Response<List<GatheringParticipantResponse>> approveList(@PathVariable Long gatheringId) {
        List<ParticipantDto> participantDtos = gatheringService.approveList(gatheringId);
        List<GatheringParticipantResponse> gatheringParticipantResponses = participantDtos.stream()
                                                                  .map(ParticipantDto::toResponse)
                                                                  .collect(Collectors.toList());
        return Response.success(gatheringParticipantResponses);
    }

    @DeleteMapping("/{gatheringId}/cancel")
    public Response<String> cancel(@PathVariable Long gatheringId,Authentication authentication) {
        String msg = gatheringService.cancel(gatheringId, authentication.getName());
        return Response.success(msg);
    }

    @GetMapping()
    public Response<Page<GatheringResponse>> findAllGatherings(@PageableDefault(size = 20,
                                                                                sort = "id", direction = Direction.DESC) Pageable pageable) {
        Page<GatheringDto> gatheringDtos = gatheringService.findAllGatherings(pageable);

        List<GatheringResponse> gatheringResponseList = new ArrayList<>();

        for (GatheringDto gatheringDto : gatheringDtos) {

            GatheringResponse gatheringResponse = GatheringResponse.createGetOne(gatheringDto);

            gatheringResponseList.add(gatheringResponse);

        }

        return Response.success(new PageImpl<>(gatheringResponseList, pageable, gatheringDtos.getTotalElements()));
    }

    @GetMapping("/{gatheringId}")
    public Response getOne(@PathVariable Long gatheringId) {

        GatheringDto oneGatheringDto = gatheringService.getOne(gatheringId);
        GatheringResponse gatheringResponse = GatheringResponse.createGetOne(oneGatheringDto);
        return Response.success(gatheringResponse);
    }

}