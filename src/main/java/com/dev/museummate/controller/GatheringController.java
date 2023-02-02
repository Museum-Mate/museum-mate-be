package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringPostResponse;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.service.GatheringService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
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
