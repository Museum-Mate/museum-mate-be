package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.ReviewEntity;
import com.dev.museummate.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Page<ReviewEntity> findAllByExhibitionId(Long exhibitionId, Pageable pageable);

    Page<ReviewEntity> findByUser(UserEntity user, Pageable pageable);
}