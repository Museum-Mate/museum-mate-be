package com.dev.museummate.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "exhibition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public ExhibitionEntity(long id, String name, String startsAt, String endsAt, String price, String ageLimit, String detailInfo, String galleryDetail, GalleryEntity gallery) {
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
}
