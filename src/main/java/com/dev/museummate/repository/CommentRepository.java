package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.CommentEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {

    Page<CommentEntity> findAllByGatheringId(Pageable pageable, Long gatheringId);
    Page<CommentEntity> findAllByGatheringIdAndParentId(Pageable pageable, Long gatheringId, Long parentId);
    List<CommentEntity> findAllByGatheringIdAndParentId(Long gatheringId, Long parentId);

}
