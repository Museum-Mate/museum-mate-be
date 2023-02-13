package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {
    //    Page<ExhibitionEntity> findAll(Pageable pageable, ExhibitionEntity exhibition);

    Page<ExhibitionEntity> findAll(Pageable pageable);
    Page<ExhibitionEntity> findAllByPrice(Pageable pageable, String price);
    Page<ExhibitionEntity> findAllByPriceNot(Pageable pageable, String price);
}
