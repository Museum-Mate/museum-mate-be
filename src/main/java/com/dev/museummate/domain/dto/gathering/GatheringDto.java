package com.dev.museummate.domain.dto.gathering;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatheringDto {

    private Long id;
    private String meetDateTime;
    private String meetLocation;
    private Integer maxPeople;
    private String title;
    private String content;
    private Boolean close;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String lastModifiedBy;

    public GatheringDto(Long id, String meetDateTime, String meetLocation, Integer maxPeople, String title, String content, Boolean close) {
        this.id = id;
        this.meetDateTime = meetDateTime;
        this.meetLocation = meetLocation;
        this.maxPeople = maxPeople;
        this.title = title;
        this.content = content;
        this.close = close;
    }

    public GatheringEntity toEntity(UserEntity user, ExhibitionEntity exhibition) {

        return GatheringEntity.builder()
                              .meetDateTime(this.meetDateTime)
                              .meetLocation(this.meetLocation)
                              .maxPeople(this.maxPeople)
                              .title(this.title)
                              .content(this.content)
                              .close(this.close)
                              .user(user)
                              .exhibition(exhibition)
                              .build();
    }
}