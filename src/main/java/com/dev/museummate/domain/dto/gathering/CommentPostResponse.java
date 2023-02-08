package com.dev.museummate.domain.dto.gathering;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPostResponse {

    private Long gatheringId;
    private String content;
}
