package com.dev.museummate.domain.dto.exhibition;

import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkDto {

    private Long id;
    private UserEntity user;
    private ExhibitionEntity exhibition;

    @Builder
    public BookmarkDto(Long id, UserEntity user, ExhibitionEntity exhibition) {
        this.id = id;
        this.user = user;
        this.exhibition = exhibition;
    }

    /**
     * Entity 객체를 Dto 객체로 변환하는 메소드
     */
    public static BookmarkDto toDto(BookmarkEntity bookmarkEntity) {

        return BookmarkDto.builder()
                .id(bookmarkEntity.getId())
                .user(bookmarkEntity.getUser())
                .exhibition(bookmarkEntity.getExhibition())
                .build();
    }
}
