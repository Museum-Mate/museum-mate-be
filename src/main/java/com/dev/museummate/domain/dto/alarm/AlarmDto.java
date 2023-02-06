package com.dev.museummate.domain.dto.alarm;

import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmDto {
    private Long id;
    private UserEntity user;
    private ExhibitionEntity exhibition;
    private String alarmMessage;

    @Builder
    public AlarmDto(Long id, UserEntity user, ExhibitionEntity exhibition, String alarmMessage) {
        this.id = id;
        this.user = user;
        this.exhibition = exhibition;
        this.alarmMessage = alarmMessage;
    }

    public static AlarmDto toDto(AlarmEntity alarm) {
        return AlarmDto.builder()
                .id(alarm.getId())
                .user(alarm.getUser())
                .exhibition(alarm.getExhibition())
                .alarmMessage(alarm.getAlarmMessage())
                .build();
    }
}
