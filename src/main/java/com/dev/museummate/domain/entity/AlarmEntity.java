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

@Entity
@Table(name = "alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String alarmMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private ExhibitionEntity exhibition;

    @Builder
    public AlarmEntity(Long id, String alarmMessage, UserEntity user, ExhibitionEntity exhibition) {
        this.id = id;
        this.alarmMessage = alarmMessage;
        this.user = user;
        this.exhibition = exhibition;
    }

    public static AlarmEntity createAlarm(UserEntity user, ExhibitionEntity exhibition, String alarmMessage){
        return AlarmEntity.builder()
                .user(user)
                .exhibition(exhibition)
                .alarmMessage(alarmMessage)
                .build();
    }
}
