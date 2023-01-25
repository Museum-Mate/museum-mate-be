package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {
    //    Page<ExhibitionEntity> findAll(Pageable pageable, ExhibitionEntity exhibition);

}
