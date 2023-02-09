package com.dev.museummate.domain.dto.gathering;

import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private UserEntity user;
    private Long parentId;
    private String content;
    private GatheringEntity gathering;
    private List<CommentDto> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse toResponse() {
        return CommentResponse.builder()
                              .id(this.id)
                              .userName(this.user.getUserName())
                              .content(this.content)
                              .createdAt(this.createdAt)
                              .updatedAt(this.updatedAt)
                              .build();
    }

    public CommentResponse toParentResponse(List<CommentResponse> commentResponses) {
        return CommentResponse.builder()
                              .id(this.id)
                              .userName(this.user.getUserName())
                              .content(this.content)
            .replies(commentResponses)
                              .createdAt(this.createdAt)
                              .updatedAt(this.updatedAt)
                              .build();
    }


}
