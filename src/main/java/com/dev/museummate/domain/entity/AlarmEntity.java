package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
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
    @NotNull
    private String alarmMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    @NotNull
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
