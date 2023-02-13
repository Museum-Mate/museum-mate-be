package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionDto {

    private Long id;

    private String name;
    private String startAt;
    private String endAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;

    private String galleryName;
    private String notice;

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
    private String detailInfoImgUrl;
    private String detailInfoUrl;

    @Builder
    public ExhibitionDto(Long id, String name, String startAt, String endAt, String price, String ageLimit, String detailInfo,
                         String galleryLocation, String galleryName, String notice, UserEntity user, String statMale, String statFemale,
                         String statAge10, String statAge20, String statAge30, String statAge40, String statAge50, String mainImgUrl,
                         String noticeImgUrl, String detailInfoImgUrl, String detailInfoUrl) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.price = price;
        this.ageLimit = ageLimit;
        this.detailInfo = detailInfo;
        this.galleryLocation = galleryLocation;
        this.galleryName = galleryName;
        this.notice = notice;
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
        this.detailInfoImgUrl = detailInfoImgUrl;
        this.detailInfoUrl = detailInfoUrl;
    }

    /**
     * Entity 객체를 Dto 객체로 변환하는 메소드
     */
    public static ExhibitionDto toDto(ExhibitionEntity exhibitionEntity) {

        return ExhibitionDto.builder()
                .id(exhibitionEntity.getId())
                .name(exhibitionEntity.getName())
                .startAt(exhibitionEntity.getStartAt())
                .endAt(exhibitionEntity.getEndAt())
                .price(exhibitionEntity.getPrice())
                .ageLimit(exhibitionEntity.getAgeLimit())
                .detailInfo(exhibitionEntity.getDetailInfo())
                .galleryLocation(exhibitionEntity.getGalleryLocation())
                .galleryName(exhibitionEntity.getGalleryName())
                .notice(exhibitionEntity.getNotice())
                .user(exhibitionEntity.getUser())
                .statMale(exhibitionEntity.getStatMale())
                .statFemale(exhibitionEntity.getStatFemale())
                .statAge10(exhibitionEntity.getStatAge_10())
                .statAge20(exhibitionEntity.getStatAge_20())
                .statAge30(exhibitionEntity.getStatAge_30())
                .statAge40(exhibitionEntity.getStatAge_40())
                .statAge50(exhibitionEntity.getStatAge_50())
                .mainImgUrl(exhibitionEntity.getMainImgUrl())
                .noticeImgUrl(exhibitionEntity.getNoticeImgUrl())
                .detailInfoImgUrl(exhibitionEntity.getDetailInfoImgUrl())
                .detailInfoUrl(exhibitionEntity.getDetailInfoUrl())
                .build();
    }
}
