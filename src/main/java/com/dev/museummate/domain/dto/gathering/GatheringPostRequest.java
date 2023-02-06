package com.dev.museummate.domain.dto.gathering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatheringPostRequest {

    private Long exhibitionId;
    private String meetDateTime;
    private String meetLocation;
    private Integer maxPeople;
    private String title;
    private String content;

    public GatheringDto toDto() {

        return GatheringDto.builder()
                           .meetDateTime(this.meetDateTime)
                           .meetLocation(this.meetLocation)
                           .maxPeople(this.maxPeople)
                           .title(this.title)
                           .content(this.content)
                           .close(Boolean.FALSE)
                           .build();
    }
}
