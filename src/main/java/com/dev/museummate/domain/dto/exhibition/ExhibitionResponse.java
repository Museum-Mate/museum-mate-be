package com.dev.museummate.domain.dto.exhibition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionResponse {

    private String name;
    private String startsAt;
    private String endsAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryDetail;
    private Long galleryId;
}
