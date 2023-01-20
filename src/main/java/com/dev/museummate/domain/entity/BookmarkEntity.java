package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private ExhibitionEntity exhibition;

    public static BookmarkEntity of(ExhibitionEntity exhibition, UserEntity user) {
        BookmarkEntity bookmark = BookmarkEntity.builder()
                .exhibition(exhibition)
                .user(user)
                .build();

        return bookmark;
    }

}
