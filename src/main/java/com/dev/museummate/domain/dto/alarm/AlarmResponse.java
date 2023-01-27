package com.dev.museummate.domain.dto.alarm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmResponse {
    private String userName;
    private String exhibitionName;
    private String alarmMessage;

    @Builder
    public AlarmResponse(String userName, String exhibitionName, String alarmMessage) {
        this.userName = userName;
        this.exhibitionName = exhibitionName;
        this.alarmMessage = alarmMessage;
    }
}
