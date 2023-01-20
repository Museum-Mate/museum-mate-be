package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @GetMapping("/{exhibitionId}")
    public Response getOne(@PathVariable long id) {
        ExhibitionResponse exhibitionResponse = exhibitionService.getOne(id);
        return Response.success(exhibitionResponse);
    }

    // 전시회 전체 조회
    @GetMapping
    public Response<Page<ExhibitionResponse>> findAllExhibitions (@PageableDefault(size = 20,
            sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(exhibitionService.findAllExhibitions(pageable));
    }

    // 전시회 단건 조회
    @GetMapping("/{exhibitionId}")
    public Response<ExhibitionResponse> findOneExhibition (@PathVariable Long id) {
        return Response.success(exhibitionService.findOneExhibition(id));
    }

    @PostMapping("/{exhibitionId}/bookmarks")
    public Response addToBookmark(@PathVariable long exhibitionId, Authentication authentication) {
        String result = exhibitionService.addToBookmark(exhibitionId, authentication.getName());
        return Response.success(result);
    }

}
