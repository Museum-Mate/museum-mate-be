package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.dto.review.EditReviewRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
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

    /*
    builder 생성자 추가
     */
    @Builder
    public ReviewEntity(Long id, String title, String content,
        Integer star, UserEntity user,
        ExhibitionEntity exhibition, String visitedDate){
        this.id = id;
        this.title = title;
        this.content = content;
        this.star = star;
        this.user = user;
        this.exhibition = exhibition;
        this.visitedDate = visitedDate;

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
