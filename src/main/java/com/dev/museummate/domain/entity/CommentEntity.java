package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ColumnDefault("0")
    @NotNull
    private Long parentId;

    @NotNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "gathering")
    private GatheringEntity gathering;

    @Builder
    public CommentEntity(Long id, UserEntity user, Long parentId, String content, GatheringEntity gathering) {
        this.id = id;
        this.user = user;
        this.parentId = parentId;
        this.content = content;
        this.gathering = gathering;
    }
}
