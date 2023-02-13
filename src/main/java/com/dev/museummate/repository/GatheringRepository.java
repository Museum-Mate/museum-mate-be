package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<GatheringEntity, Long> {

    Page<GatheringEntity> findByUser(UserEntity user, Pageable pageable);
}
