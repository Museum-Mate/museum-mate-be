package com.dev.museummate.domain.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    @NotNull
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    @NotNull
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
