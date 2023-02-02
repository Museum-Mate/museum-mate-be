package com.dev.museummate.service;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public void checkUser(UserEntity authenticatedUser, UserEntity writer) {
        if(!authenticatedUser.getId().equals(writer.getId())) throw new AppException(ErrorCode.FORBIDDEN_ACCESS, "접근할 수 없습니다.");
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

    public Page<GatheringResponse> findAllGatherings(Pageable pageable) {
        Page<GatheringEntity> gatheringEntities = gatheringRepository.findAll(pageable);

        List<GatheringResponse> gatheringResponseList = new ArrayList<>();

        for(GatheringEntity gathering : gatheringEntities) {

            GatheringDto selectedGatheringDto = GatheringDto.toDto(gathering);

            Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gathering.getId());

            GatheringResponse gatheringResponse = GatheringResponse.createGetOne(selectedGatheringDto, currentPeople);

            gatheringResponseList.add(gatheringResponse);
        }

        return new PageImpl<>(gatheringResponseList, pageable, gatheringEntities.getTotalElements());
    }

    public GatheringResponse getOne(long gatheringId) {
        GatheringEntity gatheringEntity = findPostById(gatheringId);

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);

        GatheringDto selectedGatheringDto = GatheringDto.toDto(gatheringEntity);

        GatheringResponse gatheringResponse = GatheringResponse.createGetOne(selectedGatheringDto, currentPeople);

        return gatheringResponse;
    }

    public GatheringDto edit(Long gatheringId, GatheringPostRequest gatheringPostRequest, String email) {
        UserEntity user = findUserByEmail(email);

        GatheringEntity savedGathering = findPostById(gatheringId);

        checkUser(user, savedGathering.getUser());

        savedGathering.editGathering(gatheringPostRequest.getMeetDateTime(),
                                     gatheringPostRequest.getMeetLocation(),
                                     gatheringPostRequest.getMaxPeople(),
                                     gatheringPostRequest.getTitle(),
                                     gatheringPostRequest.getContent()
        );

        return GatheringDto.toDto(savedGathering);
    }

    public Long delete(Long gatheringId, String email) {

        UserEntity user = findUserByEmail(email);

        GatheringEntity savedGathering = findPostById(gatheringId);

        checkUser(user, savedGathering.getUser());

        gatheringRepository.delete(savedGathering);

        return savedGathering.getId();
    }
}
