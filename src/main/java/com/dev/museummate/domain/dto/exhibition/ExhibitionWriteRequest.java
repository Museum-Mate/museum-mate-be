package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionWriteRequest {
    private String name;
    private String startAt;
    private String endAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;
    private String galleryName;
    private String mainImgUrl;
    private String noticeImgUrl;
    private String detailInfoImgUrl;
    private String detailInfoUrl;

    @Builder
    public ExhibitionWriteRequest(String name, String startAt, String endAt, String price, String ageLimit, String detailInfo,
                                  String galleryLocation, String galleryName, String mainImgUrl, String noticeImgUrl, String detailInfoImgUrl,
                                  String detailInfoUrl) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.price = price;
        this.ageLimit = ageLimit;
        this.detailInfo = detailInfo;
        this.galleryLocation = galleryLocation;
        this.galleryName = galleryName;
        this.mainImgUrl = mainImgUrl;
        this.noticeImgUrl = noticeImgUrl;
        this.detailInfoImgUrl = detailInfoImgUrl;
        this.detailInfoUrl = detailInfoUrl;
    }

    public ExhibitionEntity toEntity(UserEntity user) {

        return ExhibitionEntity.builder()
                .name(this.name)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .price(this.price)
                .ageLimit(this.ageLimit)
                .detailInfo(this.detailInfo)
                .galleryLocation(this.galleryLocation)
                .galleryName(this.galleryName)
                .user(user)
                .statMale(null)
                .statFemale(null)
                .statAge_10(null)
                .statAge_20(null)
                .statAge_30(null)
                .statAge_40(null)
                .statAge_50(null)
                .mainImgUrl(this.mainImgUrl)
                .noticeImgUrl(this.noticeImgUrl)
                .detailInfoImgUrl(this.detailInfoImgUrl)
                .detailInfoUrl(this.detailInfoUrl)
                .build();
    }
}
