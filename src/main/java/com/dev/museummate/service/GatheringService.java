package com.dev.museummate.service;

import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
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
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public String enroll(Long gatheringId, String email) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));

        participantRepository.findByUserIdAndGatheringId(findUser.getId(), findGatheringPost.getId())
                             .ifPresent(p -> {
                                 throw new AppException(ErrorCode.DUPLICATED_ENROLL, "이미 신청 되었습니다.");
                             });

        participantRepository.save(new ParticipantEntity(findUser, findGatheringPost, Boolean.FALSE, Boolean.FALSE));
        return "신청이 완료 되었습니다.";
    }

    public List<GatheringResponse> enrollList(Long gatheringId, String email) {

        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        if (!findUser.getId().equals(findGatheringPost.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "글 작성자만 조회 가능합니다.");
        }

        List<ParticipantEntity> findParticipantList = participantRepository.findAllByGatheringIdAndApprove(gatheringId,Boolean.FALSE);

        List<GatheringResponse> participantList = findParticipantList.stream()
                                                                     .map(ParticipantEntity::toEnrollResponse)
                                                                     .collect(Collectors.toList());
        return participantList;
    }

    public List<GatheringResponse> approveList(Long gatheringId) {
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        List<ParticipantEntity> findParticipantList = participantRepository.findAllByGatheringIdAndApprove(gatheringId,Boolean.TRUE);

        List<GatheringResponse> participantList = findParticipantList.stream()
                                                                     .map(ParticipantEntity::toEnrollResponse)
                                                                     .collect(Collectors.toList());

        return participantList;
    }
}
