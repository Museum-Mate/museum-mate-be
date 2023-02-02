package com.dev.museummate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE review SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = FALSE") // NOT to select deleted review
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
    private boolean isDeleted = Boolean.FALSE; // Soft Delete 컬럼, 삭제 여부 기본값(FALSE) 셋팅

    /*
    builder 생성자 추가
     */
    @Builder
    public ReviewEntity(Long id, String title, String content,
        Integer star, UserEntity user,
        ExhibitionEntity exhibition, String visitedDate, Boolean isDeleted){
        this.id = id;
        this.title = title;
        this.content = content;
        this.star = star;
        this.user = user;
        this.exhibition = exhibition;
        this.visitedDate = visitedDate;
        this.isDeleted = isDeleted;

    }

}
