package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private Integer star;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    @NotNull
    private ExhibitionEntity exhibition;
    private String visitedDate;

}
