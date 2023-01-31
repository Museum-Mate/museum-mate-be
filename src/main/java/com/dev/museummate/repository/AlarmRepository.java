package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    Page<AlarmEntity> findByUser(Pageable pageable, UserEntity user);
}
