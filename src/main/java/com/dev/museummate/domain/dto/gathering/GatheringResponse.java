package com.dev.museummate.domain.dto.gathering;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatheringResponse {
    private Long id;
    private String meetDateTime;
    private String meetLocation;
    private Integer currentPeople;
    private Integer maxPeople;
    private String title;
    private String content;
    private Boolean close;
    private String exhibitionName;
    private String exhibitionMainUrl;
    private String userName;
    private LocalDateTime createdAt;

    public static Page<GatheringResponse> of(Page<GatheringDto> gatherings) {
        return gatherings.map(gathering -> GatheringResponse.builder()
                                                            .id(gathering.getId())
                                                            .meetDateTime(gathering.getMeetDateTime())
                                                            .meetLocation(gathering.getMeetLocation())
                                                            .currentPeople(gathering.getCurrentPeople())
                                                            .maxPeople(gathering.getMaxPeople())
                                                            .title(gathering.getTitle())
                                                            .content(gathering.getContent())
                                                            .close(gathering.getClose())
                                                            .exhibitionName(gathering.getExhibition().getName())
                                                            .exhibitionMainUrl(gathering.getExhibition().getMainImgUrl())
                                                            .userName(gathering.getUser().getUserName())
                                                            .createdAt(gathering.getCreatedAt())
                                                            .build());
    }

    public static GatheringResponse createGetOne(GatheringDto gatheringDto) {
        return GatheringResponse.builder()
                                .id(gatheringDto.getId())
                                .meetDateTime(gatheringDto.getMeetDateTime())
                                .meetLocation(gatheringDto.getMeetLocation())
                                .currentPeople(gatheringDto.getCurrentPeople())
                                .maxPeople(gatheringDto.getMaxPeople())
                                .title(gatheringDto.getTitle())
                                .content(gatheringDto.getContent())
                                .close(gatheringDto.getClose())
                                .exhibitionName(gatheringDto.getExhibition().getName())
                                .exhibitionMainUrl(gatheringDto.getExhibition().getMainImgUrl())
                                .userName(gatheringDto.getUser().getUserName())
                                .createdAt(gatheringDto.getCreatedAt())
                                .build();
    }
}