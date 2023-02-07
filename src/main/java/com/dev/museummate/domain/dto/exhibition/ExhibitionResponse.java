package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionResponse {

    private Long id;
    private String name;
    private String startsAt;
    private String endsAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;
    private Long galleryId;
    private String statMale;
    private String statFemale;
    private String statAge10;
    private String statAge20;
    private String statAge30;
    private String statAge40;
    private String statAge50;
    private String mainImgUrl;
    private String noticeImgUrl;
    private String detailImgUrl;

    public static Page<ExhibitionResponse> of(Page<ExhibitionDto> exhibitions) {
        return exhibitions.map(exhibition -> ExhibitionResponse.builder()
                .id(exhibition.getId())
                .name(exhibition.getName())
                .startsAt(exhibition.getStartAt())
                .endsAt(exhibition.getEndAt())
                .price(exhibition.getPrice())
                .ageLimit(exhibition.getAgeLimit())
                .detailInfo(exhibition.getDetailInfo())
                .galleryLocation(exhibition.getGalleryLocation())
                .galleryId(exhibition.getGallery().getId())
                .statMale(exhibition.getStatMale())
                .statFemale(exhibition.getStatFemale())
                .statAge10(exhibition.getStatAge10())
                .statAge20(exhibition.getStatAge20())
                .statAge30(exhibition.getStatAge30())
                .statAge40(exhibition.getStatAge40())
                .statAge50(exhibition.getStatAge50())
                .mainImgUrl(exhibition.getMainImgUrl())
                .noticeImgUrl(exhibition.getNoticeImgUrl())
                .detailImgUrl(exhibition.getDetailImgUrl())
                .build());
    }

    public static ExhibitionResponse of(ExhibitionDto exhibition) {
        return ExhibitionResponse.builder()
                .id(exhibition.getId())
                .name(exhibition.getName())
                .startsAt(exhibition.getStartAt())
                .endsAt(exhibition.getEndAt())
                .price(exhibition.getPrice())
                .ageLimit(exhibition.getAgeLimit())
                .detailInfo(exhibition.getDetailInfo())
                .galleryLocation(exhibition.getGalleryLocation())
                .galleryId(exhibition.getId())
                .statMale(exhibition.getStatMale())
                .statFemale(exhibition.getStatFemale())
                .statFemale(exhibition.getStatAge10())
                .statAge10(exhibition.getStatAge10())
                .statAge20(exhibition.getStatAge20())
                .statAge30(exhibition.getStatAge30())
                .statAge40(exhibition.getStatAge40())
                .statAge50(exhibition.getStatAge50())
                .mainImgUrl(exhibition.getMainImgUrl())
                .noticeImgUrl(exhibition.getNoticeImgUrl())
                .detailImgUrl(exhibition.getDetailImgUrl())
                .build();
    }
}
