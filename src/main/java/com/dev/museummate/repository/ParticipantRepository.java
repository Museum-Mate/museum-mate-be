package com.dev.museummate.repository;

import com.dev.museummate.domain.entity.ParticipantEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    Optional<ParticipantEntity> findByUserIdAndGatheringId(Long userId, Long gatheringId);
    List<ParticipantEntity> findAllByGatheringIdAndApprove(Long gatheringId, Boolean approve);

}
