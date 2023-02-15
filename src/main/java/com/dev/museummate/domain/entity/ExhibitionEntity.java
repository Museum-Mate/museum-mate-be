package com.dev.museummate.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "exhibition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String startAt;
    private String endAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryLocation;

    @ManyToOne
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
    private String galleryName;
    private String notice;
    private String detailInfoUrl;

    @Builder
    public ExhibitionEntity(Long id, String name, String startAt, String endAt, String price, String ageLimit, String detailInfo,
                            String galleryLocation, UserEntity user, String statMale, String statFemale, String statAge_10,
                            String statAge_20,
                            String statAge_30, String statAge_40, String statAge_50, String mainImgUrl, String noticeImgUrl,
                            String detailInfoImgUrl, String galleryName, String notice, String detailInfoUrl) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.price = price;
        this.ageLimit = ageLimit;
        this.detailInfo = detailInfo;
        this.galleryLocation = galleryLocation;
        this.user = user;
        this.statMale = statMale;
        this.statFemale = statFemale;
        this.statAge_10 = statAge_10;
        this.statAge_20 = statAge_20;
        this.statAge_30 = statAge_30;
        this.statAge_40 = statAge_40;
        this.statAge_50 = statAge_50;
        this.mainImgUrl = mainImgUrl;
        this.noticeImgUrl = noticeImgUrl;
        this.detailInfoImgUrl = detailInfoImgUrl;
        this.galleryName = galleryName;
        this.notice = notice;
        this.detailInfoUrl = detailInfoUrl;
    }

    public void insertMainImgUrl(String s3Url) {
        this.mainImgUrl = s3Url;
    }
}
