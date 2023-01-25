package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
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
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @GetMapping("/{exhibitionId}")
    public Response getOne(@PathVariable Long exhibitionId) {

        ExhibitionResponse exhibitionResponse = exhibitionService.getOne(exhibitionId);

        return Response.success(exhibitionResponse);
    }
    
    // 전시회 전체 조회
    @GetMapping
    public Response<Page<ExhibitionResponse>> findAllExhibitions (@PageableDefault(size = 20,
            sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(exhibitionService.findAllExhibitions(pageable));
    }

    @PostMapping("/{exhibitionId}/bookmarks")
    public Response addToBookmark(@PathVariable Long exhibitionId, Authentication authentication) {

        BookmarkAddResponse bookmarkAddResponse = exhibitionService.addToBookmark(exhibitionId, authentication.getName());

        return Response.success(bookmarkAddResponse);
    }

}
