package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringPostResponse;
import com.dev.museummate.service.GatheringService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/gathering")
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
    public Response<List<GatheringResponse>> enrollList(@PathVariable Long gatheringId, Authentication authentication) {
        List<GatheringResponse> enrollList = gatheringService.enrollList(gatheringId, authentication.getName());
        return Response.success(enrollList);
    }

    @GetMapping("/{gatheringId}/approve/list")
    public Response<List<GatheringResponse>> approveList(@PathVariable Long gatheringId) {
        List<GatheringResponse> approveList = gatheringService.approveList(gatheringId);
        return Response.success(approveList);
    }

    @DeleteMapping("/{gatheringId}/cancel")
    public Response<String> cancel(@PathVariable Long gatheringId,Authentication authentication) {
        String msg = gatheringService.cancel(gatheringId, authentication.getName());
        return Response.success(msg);
    }

}
