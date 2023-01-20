package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    //
    Optional<BookmarkEntity> findByExhibitionAndUser(ExhibitionEntity exhibition, UserEntity user);

}
