package com.dev.museummate.domain.dto.gathering;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatheringResponse {
    private Long id;
    private String meetDateTime;
    private String meetLocation;
    private Integer maxPeople;
    private String title;
    private String content;
    private Boolean close;
    private Long exhibitionId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String lastModifiedBy;

    public static Page<GatheringResponse> of(Page<GatheringDto> gatherings) {
        return gatherings.map(gathering -> GatheringResponse.builder()
            .id(gathering.getId())
            .meetDateTime(gathering.getMeetDateTime())
            .meetLocation(gathering.getMeetLocation())
            .maxPeople(gathering.getMaxPeople())
            .title(gathering.getTitle())
            .content(gathering.getContent())
            .close(gathering.getClose())
            .exhibitionId(gathering.getExhibition().getId())
            .userId(gathering.getUser().getId())
            .createdAt(gathering.getCreatedAt())
            .lastModifiedAt(gathering.getLastModifiedAt())
            .deletedAt(gathering.getDeletedAt())
            .createdBy(gathering.getCreatedBy())
            .lastModifiedBy(gathering.getLastModifiedBy())
            .build());
    }

}
