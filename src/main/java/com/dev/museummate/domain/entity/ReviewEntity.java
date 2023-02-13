package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.dto.review.EditReviewRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
@SQLDelete(sql = "UPDATE review SET is_deleted = TRUE, deleted_at = now() WHERE id = ?")
@Where(clause = "is_deleted = FALSE") // NOT to select deleted review
public class ReviewEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Integer star;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private ExhibitionEntity exhibition;
    private String visitedDate;
    private Boolean isDeleted = Boolean.FALSE; // Soft Delete 컬럼, 삭제 여부 기본값(FALSE) 셋팅

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

    @Builder
    public ReviewEntity(Long id, String title, String content,
                        Integer star, UserEntity user,
                        ExhibitionEntity exhibition, String visitedDate,
                        String lastModifiedBy, LocalDateTime lastModifiedAt
                        ){
        this.id = id;
        this.title = title;
        this.content = content;
        this.star = star;
        this.user = user;
        this.exhibition = exhibition;
        this.visitedDate = visitedDate;
    }


    /*
    Create Method for Edit Review
     */

    public void editReview(ReviewEntity editedReview) {
        this.title = editedReview.getTitle();
        this.content = editedReview.getContent();
        this.star = editedReview.getStar();
        this.visitedDate = editedReview.getVisitedDate();
    }

    public static ReviewEntity toEntity(EditReviewRequest editReviewRequest) {
        return ReviewEntity.builder()
            .title(editReviewRequest.getNewTitle())
            .content(editReviewRequest.getNewContent())
            .star(editReviewRequest.getNewStar())
            .visitedDate(editReviewRequest.getNewVisitedDate())
                           .build();
    }

}
