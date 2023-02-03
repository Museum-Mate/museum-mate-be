package com.dev.museummate.domain.dto.gathering;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatheringParticipantResponse {

    private Long participantId;
    private String userName;
    private Boolean approve;
    private LocalDateTime createdAt;

}
