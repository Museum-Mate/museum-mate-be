package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private ExhibitionEntity exhibition;

    @Builder
    public BookmarkEntity(long id, UserEntity user, ExhibitionEntity exhibition) {
        this.id = id;
        this.user = user;
        this.exhibition = exhibition;
    }

    public static BookmarkEntity CreateBookmark(ExhibitionEntity exhibition, UserEntity user) {

        return BookmarkEntity.builder()
                .exhibition(exhibition)
                .user(user)
                .build();
    }

}
