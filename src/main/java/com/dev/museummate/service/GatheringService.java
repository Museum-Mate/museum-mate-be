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
import jakarta.transaction.Transactional;
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
        GatheringEntity savedGathering = gatheringRepository.save(gatheringEntity);
        GatheringDto savedDto = savedGathering.of();

        participantRepository.save(ParticipantEntity.of(findUser, savedGathering, Boolean.TRUE, Boolean.TRUE));

        return savedDto;
    }

    public String enroll(Long gatheringId, String email) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        if (findGatheringPost.getClose().equals(Boolean.TRUE)) {
            throw new AppException(ErrorCode.FORBIDDEN_ACCESS, "모집이 종료된 게시글 입니다.");
        }

        participantRepository.findByUserIdAndGatheringId(findUser.getId(), findGatheringPost.getId())
                             .ifPresent(p -> {
                                 throw new AppException(ErrorCode.DUPLICATED_ENROLL, "이미 신청 되었습니다.");
                             });

        participantRepository.save(ParticipantEntity.of(findUser, findGatheringPost, Boolean.FALSE, Boolean.FALSE));
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

    @Transactional
    public String approve(Long gatheringId, Long participantId, String email) {

        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        if (!findUser.getId().equals(findGatheringPost.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "글 작성자만 조회 가능합니다.");
        }

        ParticipantEntity participant = participantRepository.findById(participantId)
                                                             .orElseThrow(() -> new AppException(ErrorCode.PARTICIPANT_NOT_FOUND,
                                                                                                 "존재하지 않는 참여자 입니다."));
        participant.approveUser();
        participantRepository.save(participant);

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);

        if (currentPeople.equals(findGatheringPost.getMaxPeople())) {
            findGatheringPost.closePost();
            gatheringRepository.save(findGatheringPost);
        }

        return "신청을 승인 했습니다.";
    }

    public String cancel(Long gatheringId, String email) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        ParticipantEntity participant = participantRepository.findByUserIdAndGatheringId(findUser.getId(), findGatheringPost.getId())
                                                             .orElseThrow(() -> new AppException(ErrorCode.PARTICIPANT_NOT_FOUND,
                                                                                                 "신청자를 찾을 수 없습니다."));
        participantRepository.delete(participant);

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);
        if (!currentPeople.equals(findGatheringPost.getMaxPeople())) {
            findGatheringPost.openPost();
            gatheringRepository.save(findGatheringPost);
        }
        return "신청이 취소 되었습니다.";

    }
}
