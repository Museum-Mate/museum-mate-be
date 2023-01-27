package com.dev.museummate.controller;

import com.dev.museummate.configuration.Response;
import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionWriteRequest;
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

        ExhibitionDto exhibitionDto = exhibitionService.getOne(exhibitionId);

        return Response.success(ExhibitionResponse.builder()
                .id(exhibitionDto.getId())
                .name(exhibitionDto.getName())
                .startsAt(exhibitionDto.getStartsAt())
                .endsAt(exhibitionDto.getEndsAt())
                .price(exhibitionDto.getPrice())
                .ageLimit(exhibitionDto.getAgeLimit())
                .detailInfo(exhibitionDto.getDetailInfo())
                .galleryLocation(exhibitionDto.getGalleryLocation())
                .galleryId(exhibitionDto.getGallery().getId())
                .statMale(exhibitionDto.getStatMale())
                .statFemale(exhibitionDto.getStatFemale())
                .statAge10(exhibitionDto.getStatAge10())
                .statAge20(exhibitionDto.getStatAge20())
                .statAge30(exhibitionDto.getStatAge30())
                .statAge40(exhibitionDto.getStatAge40())
                .statAge50(exhibitionDto.getStatAge50())
                .mainImgUrl(exhibitionDto.getMainImgUrl())
                .noticeImgUrl(exhibitionDto.getNoticeImgUrl())
                .detailImgUrl(exhibitionDto.getDetailImgUrl())
                .build());
    }
    
    // 전시회 전체 조회
    @GetMapping
    public Response<Page<ExhibitionResponse>> findAllExhibitions (@PageableDefault(size = 20,
            sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ExhibitionDto> exhibitionDtos = exhibitionService.findAllExhibitions(pageable);
        return Response.success(ExhibitionResponse.of(exhibitionDtos));
    }

    // 전시회 등록
    @PostMapping("/new")
    public Response write(@RequestBody ExhibitionWriteRequest exhibitionWriteRequest, Authentication authentication) {

        ExhibitionDto exhibitionDto = exhibitionService.write(exhibitionWriteRequest, authentication.getName());

        return Response.success(ExhibitionResponse.builder()
                .id(exhibitionDto.getId())
                .name(exhibitionDto.getName())
                .startsAt(exhibitionDto.getStartsAt())
                .endsAt(exhibitionDto.getEndsAt())
                .price(exhibitionDto.getPrice())
                .ageLimit(exhibitionDto.getAgeLimit())
                .detailInfo(exhibitionDto.getDetailInfo())
                .galleryLocation(exhibitionDto.getGalleryLocation())
                .galleryId(exhibitionDto.getGallery().getId())
                .statMale(exhibitionDto.getStatMale())
                .statFemale(exhibitionDto.getStatFemale())
                .statAge10(exhibitionDto.getStatAge10())
                .statAge20(exhibitionDto.getStatAge20())
                .statAge30(exhibitionDto.getStatAge30())
                .statAge40(exhibitionDto.getStatAge40())
                .statAge50(exhibitionDto.getStatAge50())
                .mainImgUrl(exhibitionDto.getMainImgUrl())
                .noticeImgUrl(exhibitionDto.getNoticeImgUrl())
                .detailImgUrl(exhibitionDto.getDetailImgUrl())
                .build());
    }

    // 북마크 추가
    @PostMapping("/{exhibitionId}/bookmarks")
    public Response addToBookmark(@PathVariable Long exhibitionId, Authentication authentication) {

        BookmarkAddResponse bookmarkAddResponse = exhibitionService.addToBookmark(exhibitionId, authentication.getName());

        return Response.success(bookmarkAddResponse);
    }

}
