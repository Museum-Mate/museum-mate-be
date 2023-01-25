package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionDto {

    private Long id;

    private String name;
    private String startsAt;
    private String endsAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private GalleryEntity gallery;

    @Builder
    public ExhibitionDto(Long id, String name, String startsAt, String endsAt, String price, String ageLimit, String detailInfo, String galleryDetail, GalleryEntity gallery) {
        this.id = id;
        this.name = name;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.price = price;
        this.ageLimit = ageLimit;
        this.detailInfo = detailInfo;
        this.galleryDetail = galleryDetail;
        this.gallery = gallery;
    }

    /**
     * Entity 객체를 Dto 객체로 변환하는 메소드
     */
    public static ExhibitionDto toDto(ExhibitionEntity exhibitionEntity) {

        return ExhibitionDto.builder()
                .id(exhibitionEntity.getId())
                .name(exhibitionEntity.getName())
                .startsAt(exhibitionEntity.getStartsAt())
                .endsAt(exhibitionEntity.getEndsAt())
                .price(exhibitionEntity.getPrice())
                .ageLimit(exhibitionEntity.getAgeLimit())
                .detailInfo(exhibitionEntity.getDetailInfo())
                .galleryDetail(exhibitionEntity.getGalleryDetail())
                .gallery(exhibitionEntity.getGallery())
                .build();
    }
}
