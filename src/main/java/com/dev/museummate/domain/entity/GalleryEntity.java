package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Table(name = "gallery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GalleryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String openTime;
    @NotNull
    private String closeTime;

    @Builder
    public GalleryEntity(long id, String name, String address, String openTime, String closeTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
