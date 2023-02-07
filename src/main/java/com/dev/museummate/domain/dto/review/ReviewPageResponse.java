package com.dev.museummate.domain.dto.review;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPageResponse {

    private List<ReviewDto> content;
    private Pageable pageable;

    @Builder
    public ReviewPageResponse(List<ReviewDto> content, Pageable pageable) {
        this.content = content;
        this.pageable = pageable;
    }
}
