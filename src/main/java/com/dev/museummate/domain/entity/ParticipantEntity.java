package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.dto.gathering.ParticipantDto;
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
@Table(name = "participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE participant SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class ParticipantEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "gathering_id")
    private GatheringEntity gathering;
    private Boolean hostFlag;

    private Boolean approve;

    @Builder
    public ParticipantEntity(UserEntity user, GatheringEntity gathering, Boolean hostFlag, Boolean approve) {
        this.user = user;
        this.gathering = gathering;
        this.hostFlag = hostFlag;
        this.approve = approve;
    }
    
    public static ParticipantEntity of(UserEntity findUser, GatheringEntity findGathering, Boolean hostFlag, Boolean approve) {
        return ParticipantEntity.builder()
                                .user(findUser)
                                .gathering(findGathering)
                                .hostFlag(hostFlag)
                                .approve(approve)
                                .build();
    }

    public ParticipantDto toDto() {
        return ParticipantDto.builder()
                             .id(this.id)
                             .user(this.user)
                             .gathering(this.gathering)
                             .hostFlag(this.hostFlag)
                             .approve(this.approve)
                             .createdAt(this.getCreatedAt())
                             .build();
    }

    public void approveUser() {
        this.approve = Boolean.TRUE;
    }
    
}