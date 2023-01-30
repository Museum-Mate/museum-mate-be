package com.dev.museummate.domain.entity;

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
    @JoinColumn(name = "socialing_id")
    private GatheringEntity gathering;
    @NotNull
    private Boolean hostFlag;

    @Builder
    public ParticipantEntity(UserEntity user, GatheringEntity gathering, Boolean hostFlag) {
        this.user = user;
        this.gathering = gathering;
        this.hostFlag = hostFlag;
    }
}
