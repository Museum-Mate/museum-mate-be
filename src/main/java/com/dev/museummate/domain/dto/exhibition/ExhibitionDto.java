package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
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
    private String galleryLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private GalleryEntity gallery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

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

    @Builder
    public ExhibitionDto(Long id, String name, String startsAt, String endsAt, String price, String ageLimit, String detailInfo, String galleryLocation, GalleryEntity gallery, UserEntity user,
                         String statMale, String statFemale, String statAge10, String statAge20, String statAge30, String statAge40, String statAge50, String mainImgUrl, String noticeImgUrl, String detailImgUrl) {
        this.id = id;
        this.name = name;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.price = price;
        this.ageLimit = ageLimit;
        this.detailInfo = detailInfo;
        this.galleryLocation = galleryLocation;
        this.gallery = gallery;
        this.user = user;
        this.statMale = statMale;
        this.statFemale = statFemale;
        this.statAge10 = statAge10;
        this.statAge20 = statAge20;
        this.statAge30 = statAge30;
        this.statAge40 = statAge40;
        this.statAge50 = statAge50;
        this.mainImgUrl = mainImgUrl;
        this.noticeImgUrl = noticeImgUrl;
        this.detailImgUrl = detailImgUrl;
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
                .galleryLocation(exhibitionEntity.getGalleryLocation())
                .gallery(exhibitionEntity.getGallery())
                .user(exhibitionEntity.getUser())
                .statMale(exhibitionEntity.getStatMale())
                .statFemale(exhibitionEntity.getStatFemale())
                .statAge10(exhibitionEntity.getStatAge10())
                .statAge20(exhibitionEntity.getStatAge20())
                .statAge30(exhibitionEntity.getStatAge30())
                .statAge40(exhibitionEntity.getStatAge40())
                .statAge50(exhibitionEntity.getStatAge50())
                .mainImgUrl(exhibitionEntity.getMainImgUrl())
                .noticeImgUrl(exhibitionEntity.getNoticeImgUrl())
                .detailImgUrl(exhibitionEntity.getDetailImgUrl())
                .build();
    }
}
