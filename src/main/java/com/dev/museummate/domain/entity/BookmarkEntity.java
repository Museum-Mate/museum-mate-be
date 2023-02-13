package com.dev.museummate.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkEntity extends BaseTimeEntity {

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
