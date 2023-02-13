package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExhibitionEditRequest {

    private Long id;

    private String name;
    private String startAt;
    private String endAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;
    private String galleryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String statMale;
    private String statFemale;
    private String statAge_10;
    private String statAge_20;
    private String statAge_30;
    private String statAge_40;
    private String statAge_50;
    private String mainImgUrl;
    private String noticeImgUrl;
    private String detailInfoImgUrl;
    private String detailInfoUrl;

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
