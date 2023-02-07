package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GalleryEntity;
import com.dev.museummate.domain.entity.UserEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
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
