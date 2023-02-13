package com.dev.museummate.domain.dto.exhibition;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkAddResponse {

    private String message;
    private Long exhibitionId;

    @Builder
    public BookmarkAddResponse(String message, Long exhibitionId) {
        this.message = message;
        this.exhibitionId = exhibitionId;
    }
}
