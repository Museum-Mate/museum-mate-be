package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{exhibitionId}/bookmark")
    public Response addToBookmark(@PathVariable long exhibitionId, Authentication authentication) {
        String result = exhibitionService.addToBookmark(exhibitionId, authentication.getName());
        return Response.success(result);
    }

}
