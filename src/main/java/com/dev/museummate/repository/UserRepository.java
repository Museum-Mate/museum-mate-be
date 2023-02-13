package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailAndName(String email, String name);
}
