package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExhibitionWriteRequest {
    private String name;
    private String startAt;
    private String endAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;
    private GalleryEntity gallery;
    private String mainImgUrl;
    private String noticeImgUrl;
    private String detailImgUrl;

    public ExhibitionEntity toEntity(UserEntity user) {

        return ExhibitionEntity.builder()
                .name(this.name)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .price(this.price)
                .ageLimit(this.ageLimit)
                .detailInfo(this.detailInfo)
                .galleryLocation(this.galleryLocation)
                .gallery(this.gallery)
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
                .detailImgUrl(this.detailImgUrl)
                .build();
    }
}
