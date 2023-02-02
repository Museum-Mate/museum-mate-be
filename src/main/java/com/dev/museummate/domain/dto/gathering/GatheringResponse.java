package com.dev.museummate.domain.dto.gathering;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatheringResponse {

    private Long participantId;
    private String userName;
    private Boolean approve;
    private LocalDateTime creatAt;
}
