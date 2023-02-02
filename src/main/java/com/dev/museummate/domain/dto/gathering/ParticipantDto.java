package com.dev.museummate.domain.dto.gathering;

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
public class ParticipantDto {
    private Long id;
    private UserEntity user;
    private GatheringEntity gathering;
    private Boolean hostFlag;
    private Boolean approve;
    private LocalDateTime createdAt;

    public GatheringResponse toResponse() {
        return GatheringResponse.builder()
                                .participantId(this.id)
                                .userName(this.user.getUserName())
                                .creatAt(this.getCreatedAt())
                                .approve(this.approve)
                                .build();

    }

}
