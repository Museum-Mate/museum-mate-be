package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_exhibition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeExhibitionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String startAt;
    private String endAt;
    private String detailInfoURL;

    @Builder
    public FreeExhibitionEntity(Long id, String name, String startAt, String endAt, String detailInfoURL) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.detailInfoURL = detailInfoURL;
    }
}
