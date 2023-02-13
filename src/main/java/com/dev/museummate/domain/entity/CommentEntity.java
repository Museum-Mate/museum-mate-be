package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.dto.gathering.CommentDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comment SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ColumnDefault("0")
    private Long parentId;

    private String content;

    @ManyToOne
    @JoinColumn(name = "gathering_id")
    private GatheringEntity gathering;

    @Builder
    public CommentEntity(Long id, UserEntity user, Long parentId, String content, GatheringEntity gathering) {
        this.id = id;
        this.user = user;
        this.parentId = parentId;
        this.content = content;
        this.gathering = gathering;
    }

    public static CommentEntity of(String comment, GatheringEntity findGathering, UserEntity findUser) {
        return CommentEntity.builder()
                            .user(findUser)
                            .content(comment)
                            .gathering(findGathering)
                            .parentId(0L)
                            .build();
    }

    public static CommentEntity ofReply(String comment, GatheringEntity findGathering, UserEntity findUser,Long commentId) {
        return CommentEntity.builder()
                            .user(findUser)
                            .content(comment)
                            .gathering(findGathering)
                            .parentId(commentId)
                            .build();
    }

    public CommentDto toDto() {
        return CommentDto.builder()
                         .id(this.id)
                         .user(this.user)
                         .createdAt(this.getCreatedAt())
                         .updatedAt(this.getLastModifiedAt())
                         .content(this.content)
                         .gathering(this.gathering)
                         .parentId(this.parentId)
                         .build();
    }

    public CommentDto toParentDto(List<CommentDto> findReplies) {
        return CommentDto.builder()
                         .id(this.id)
                         .user(this.user)
                         .createdAt(this.getCreatedAt())
                         .updatedAt(this.getLastModifiedAt())
                         .content(this.content)
                         .replies(findReplies)
                         .gathering(this.gathering)
                         .parentId(this.parentId)
                         .build();
    }

    public void update(String content) {
        this.content = content;
    }
}
