package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "gathering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE gathering SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class GatheringEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String meetDateTime;
    private String meetLocation;
    private Integer maxPeople;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private Boolean close;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private ExhibitionEntity exhibition;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder
    public GatheringEntity(Long id, String meetDateTime, String meetLocation, Integer maxPeople, String title, String content,
                           Boolean close,
                           ExhibitionEntity exhibition, UserEntity user) {
        this.id = id;
        this.meetDateTime = meetDateTime;
        this.meetLocation = meetLocation;
        this.maxPeople = maxPeople;
        this.title = title;
        this.content = content;
        this.close = close;
        this.exhibition = exhibition;
        this.user = user;
    }

    public GatheringDto of() {
        return GatheringDto.builder()
                           .id(this.id)
                           .title(this.title)
                           .content(this.content)
                           .meetLocation(this.meetLocation)
                           .meetDateTime(this.meetDateTime)
                           .maxPeople(this.maxPeople)
                           .close(this.close)
                           .createdAt(this.getCreatedAt())
                           .lastModifiedAt(this.getLastModifiedAt())
                           .lastModifiedBy(this.getLastModifiedBy())
                           .createdBy(this.getCreatedBy())
                           .build();
    }


    public void openPost() {
        this.close = Boolean.FALSE;
    }

    public void closePost() {
        this.close = Boolean.TRUE;
    }

    public void editGathering(String meetDateTime, String meetLocation, Integer maxPeople, String title, String content) {
        this.meetDateTime = meetDateTime;
        this.meetLocation = meetLocation;
        this.maxPeople = maxPeople;
        this.title = title;
        this.content = content;
    }
}