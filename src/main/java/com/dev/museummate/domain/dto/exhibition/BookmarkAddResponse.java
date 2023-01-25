package com.dev.museummate.domain.dto.exhibition;

import lombok.*;

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
