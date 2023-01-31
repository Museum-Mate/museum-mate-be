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
    private String startsAt;
    private String endsAt;
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
                .startsAt(this.startsAt)
                .endsAt(this.endsAt)
                .price(this.price)
                .ageLimit(this.ageLimit)
                .detailInfo(this.detailInfo)
                .galleryLocation(this.galleryLocation)
                .gallery(this.gallery)
                .user(user)
                .statMale(null)
                .statFemale(null)
                .statAge10(null)
                .statAge20(null)
                .statAge30(null)
                .statAge40(null)
                .statAge50(null)
                .mainImgUrl(this.mainImgUrl)
                .noticeImgUrl(this.noticeImgUrl)
                .detailImgUrl(this.detailImgUrl)
                .build();
    }
}
