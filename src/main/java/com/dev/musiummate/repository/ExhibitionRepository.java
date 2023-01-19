package com.dev.musiummate.repository;

import com.dev.musiummate.domain.entity.ExhibitionEntity;
import com.dev.musiummate.domain.entity.GalleryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {
}
