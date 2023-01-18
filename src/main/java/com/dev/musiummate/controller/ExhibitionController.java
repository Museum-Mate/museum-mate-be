package com.dev.musiummate.controller;

import com.dev.musiummate.configuration.Response;
import com.dev.musiummate.domain.dto.ExhibitionResponse;
import com.dev.musiummate.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
