package com.dev.museummate.domain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "exhibition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String startsAt;
    @NotNull
    private String endsAt;
    @NotNull
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;

    @ManyToOne
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private GalleryEntity gallery;

    @Builder
    public ExhibitionEntity(long id, String name, String startsAt, String endsAt, String price, String ageLimit, String detailInfo, String galleryLocation, GalleryEntity gallery, UserEntity user,
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
}
