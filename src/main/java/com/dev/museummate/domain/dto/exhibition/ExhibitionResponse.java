package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionResponse {

    private Long id;
    private String name;
    private String startsAt;
    private String endsAt;
    private String price;
    private String ageLimit;
    private String detailInfo;
    private String galleryDetail;
    private long galleryId;

    public static Page<ExhibitionResponse> of(Page<ExhibitionEntity> exhibitions) {
        return exhibitions.map(exhibition -> ExhibitionResponse.builder()
                .id(exhibition.getId())
                .name(exhibition.getName())
                .startsAt(exhibition.getStartsAt())
                .endsAt(exhibition.getEndsAt())
                .price(exhibition.getPrice())
                .ageLimit(exhibition.getAgeLimit())
                .detailInfo(exhibition.getDetailInfo())
                .galleryDetail(exhibition.getGalleryDetail())
                .galleryId(exhibition.getGallery().getId())
                .build()
        );
    }

    public static ExhibitionResponse of(ExhibitionEntity exhibition) {
        return ExhibitionResponse.builder()
                .id(exhibition.getId())
                .name(exhibition.getName())
                .startsAt(exhibition.getStartsAt())
                .endsAt(exhibition.getEndsAt())
                .price(exhibition.getPrice())
                .ageLimit(exhibition.getAgeLimit())
                .detailInfo(exhibition.getDetailInfo())
                .galleryDetail(exhibition.getGalleryDetail())
                .galleryId(exhibition.getId())
                .build();
    }
}