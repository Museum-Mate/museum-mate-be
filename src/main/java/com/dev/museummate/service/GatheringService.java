package com.dev.museummate.service;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.ParticipantEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.GatheringRepository;
import com.dev.museummate.repository.ParticipantRepository;
import com.dev.museummate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ParticipantRepository participantRepository;
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                                                                 new AppException(ErrorCode.EMAIL_NOT_FOUND, String.format("%s님은 존재하지 않습니다.", email)));
    }

    public GatheringEntity findPostById(long id) {
        return gatheringRepository.findById(id).orElseThrow(() ->
                                                                new AppException(ErrorCode.NOT_FOUND_POST, "존재하지 않는 게시물입니다."));
    }

    public GatheringDto posts(GatheringPostRequest gatheringPostRequest, String email) {

        UserEntity findUser = findUserByEmail(email);
        ExhibitionEntity findExhibition = exhibitionRepository.findById(gatheringPostRequest.getExhibitionId())
                                                              .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND,
                                                                                                  "존재하지 않는 전시회입니다."));

        GatheringDto gatheringDto = gatheringPostRequest.toDto();
        GatheringEntity gatheringEntity = gatheringDto.toEntity(findUser, findExhibition);
        GatheringEntity savedEntity = gatheringRepository.save(gatheringEntity);
        GatheringDto savedDto = savedEntity.of();

        participantRepository.save(new ParticipantEntity(findUser, savedEntity, Boolean.TRUE,Boolean.TRUE));

        return savedDto;
    }

    public Page<GatheringDto> findAllGatherings(Pageable pageable) {
        Page<GatheringEntity> gatheringEntities = gatheringRepository.findAll(pageable);
        return gatheringEntities.map(gathering -> GatheringDto.toDto(gathering));
    }

    public GatheringDto getOne(long gatheringId) {
        GatheringEntity gatheringEntity = findPostById(gatheringId);

        GatheringDto selectedGatheringDto = GatheringDto.toDto(gatheringEntity);

        return selectedGatheringDto;
    }
}
