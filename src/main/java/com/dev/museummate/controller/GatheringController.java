package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringPostResponse;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping()
    public Response<Page<GatheringResponse>> findAllGatherings(@PageableDefault(size = 20,
            sort = "id", direction = Direction.DESC) Pageable pageable) {
        Page<GatheringResponse> gatheringResponses = gatheringService.findAllGatherings(pageable);
        return Response.success(gatheringResponses);
    }

    @GetMapping("/{gatheringId}")
    public Response getOne(@PathVariable Long gatheringId) {

        GatheringResponse gatheringResponse = gatheringService.getOne(gatheringId);

        return Response.success(GatheringResponse.builder()
                                                 .id(gatheringResponse.getId())
                                                 .meetDateTime(gatheringResponse.getMeetDateTime())
                                                 .meetLocation(gatheringResponse.getMeetLocation())
                                                 .currentPeople(gatheringResponse.getCurrentPeople())
                                                 .maxPeople(gatheringResponse.getMaxPeople())
                                                 .title(gatheringResponse.getTitle())
                                                 .content(gatheringResponse.getContent())
                                                 .close(gatheringResponse.getClose())
                                                 .exhibitionName(gatheringResponse.getExhibitionName())
                                                 .exhibitionMainUrl(gatheringResponse.getExhibitionMainUrl())
                                                 .userName(gatheringResponse.getUserName())
                                                 .createdAt(gatheringResponse.getCreatedAt())
                                                 .build());
    }

    @PutMapping("/{gatheringId}")
    public Response edit(@PathVariable Long gatheringId, @RequestBody GatheringPostRequest gatheringPostRequest,
                         Authentication authentication) {

        GatheringDto gatheringDto = gatheringService.edit(gatheringId, gatheringPostRequest, authentication.getName());
        return Response.success(new GatheringPostResponse(gatheringDto.getId()));
    }

    @DeleteMapping("/{gatheringId}")
    public Response delete(@PathVariable Long gatheringId, Authentication authentication) {

        Long deletedId = gatheringService.delete(gatheringId, authentication.getName());
        return Response.success(new GatheringPostResponse(deletedId));
    }
}
