package com.dev.museummate.service;

import com.dev.museummate.domain.dto.gathering.CommentDto;
import com.dev.museummate.domain.dto.gathering.CommentRequest;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.ParticipantDto;
import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.CommentEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.ParticipantEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.AlarmRepository;
import com.dev.museummate.repository.CommentRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.GatheringRepository;
import com.dev.museummate.repository.ParticipantRepository;
import com.dev.museummate.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ParticipantRepository participantRepository;
    private final AlarmRepository alarmRepository;

    private final CommentRepository commentRepository;
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

        participantRepository.save(new ParticipantEntity(findUser, findGatheringPost, Boolean.FALSE, Boolean.FALSE));
        return "신청이 완료 되었습니다.";
    }

    public List<ParticipantDto> enrollList(Long gatheringId, String email) {

        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));
        if (!findUser.getId().equals(findGatheringPost.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "글 작성자만 조회 가능합니다.");
        }

        List<ParticipantEntity> findParticipantList = participantRepository.findAllByGatheringIdAndApprove(gatheringId,Boolean.FALSE);

        List<ParticipantDto> participantDtos = findParticipantList.stream()
                                                                  .map(ParticipantEntity::toDto)
                                                                  .collect(Collectors.toList());
        return participantDtos;
    }

    public List<ParticipantDto> approveList(Long gatheringId) {

        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));

        List<ParticipantEntity> findParticipantList = participantRepository.findAllByGatheringIdAndApprove(gatheringId,Boolean.TRUE);

        List<ParticipantDto> participantDtos = findParticipantList.stream()
                                                                  .map(ParticipantEntity::toDto)
                                                                  .collect(Collectors.toList());

        return participantDtos;
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
        if (findGatheringPost.getClose().equals(Boolean.TRUE)) {
            throw new AppException(ErrorCode.FORBIDDEN_ACCESS, "신청 인원이 전부 찼습니다.");
        }
        participant.approveUser();
        participantRepository.save(participant);

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);

        if (currentPeople.equals(findGatheringPost.getMaxPeople())) {
            findGatheringPost.closePost();
            gatheringRepository.save(findGatheringPost);
        }

        AlarmEntity savedAlarm = AlarmEntity.createAlarm(participant.getUser(), findGatheringPost.getExhibition(),
                                                    String.format("모집글 %s에 신청이 승인 되었습니다",
                                                                  findGatheringPost.getTitle()));
        alarmRepository.save(savedAlarm);

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

    public Page<GatheringDto> findAllGatherings(Pageable pageable) {
        Page<GatheringEntity> gatheringEntities = gatheringRepository.findAll(pageable);

        List<GatheringDto> gatheringList = new ArrayList<>();

        for(GatheringEntity gathering : gatheringEntities) {

            Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gathering.getId());

            GatheringDto selectedGatheringDto = GatheringDto.toDto(gathering,currentPeople);

            gatheringList.add(selectedGatheringDto);
        }

        return new PageImpl<>(gatheringList, pageable, gatheringEntities.getTotalElements());
    }

    public GatheringDto getOne(long gatheringId) {
        GatheringEntity findGatheringPost = gatheringRepository.findById(gatheringId)
                                                               .orElseThrow(() -> new AppException(ErrorCode.GATHERING_POST_NOT_FOUND,
                                                                                                   "존재하지 않는 모집 글 입니다."));

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);

        GatheringDto selectedGatheringDto = GatheringDto.toDto(findGatheringPost, currentPeople);

        return selectedGatheringDto;
    }

    public GatheringDto edit(Long gatheringId, GatheringPostRequest gatheringPostRequest, String email) {
        UserEntity user = findUserByEmail(email);

        GatheringEntity savedGathering = findPostById(gatheringId);

        checkUser(user, savedGathering.getUser());

        Integer currentPeople = participantRepository.countByGatheringIdAndApproveTrue(gatheringId);
        if (currentPeople > gatheringPostRequest.getMaxPeople()) {
            throw new AppException(ErrorCode.CONFLICT, "현재 승인 된 인원 보다 적게 설정할 수 없습니다.");
        }

        savedGathering.editGathering(gatheringPostRequest.getMeetDateTime(),
                                     gatheringPostRequest.getMeetLocation(),
                                     gatheringPostRequest.getMaxPeople(),
                                     gatheringPostRequest.getTitle(),
                                     gatheringPostRequest.getContent()
        );

        GatheringEntity modifiedGathering = gatheringRepository.save(savedGathering);

        if (!currentPeople.equals(modifiedGathering.getMaxPeople())) {
            modifiedGathering.openPost();
            gatheringRepository.save(modifiedGathering);
        }

        return GatheringDto.toDto(savedGathering,currentPeople);
    }

    @Transactional
    public Long delete(Long gatheringId, String email) {

        UserEntity user = findUserByEmail(email);

        GatheringEntity savedGathering = findPostById(gatheringId);

        checkUser(user, savedGathering.getUser());

        participantRepository.deleteAllByGathering(savedGathering);

        gatheringRepository.deleteById(savedGathering.getId());

        return savedGathering.getId();
    }

    public CommentDto writeComment(Long gatheringId, CommentRequest commentRequest, String email) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGathering = findPostById(gatheringId);
        CommentEntity comment = CommentEntity.of(commentRequest.getComment(), findGathering, findUser);
        CommentEntity savedComment = commentRepository.save(comment);
        CommentDto commentDto = savedComment.toDto();
        return commentDto;
    }

    public Page<CommentDto> getComments(Pageable pageable, Long gatheringId) {
        Page<CommentEntity> findComments = commentRepository.findAllByGatheringIdAndParentId(pageable, gatheringId, 0L);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (CommentEntity findComment : findComments) {
            List<CommentEntity> findReplies = commentRepository.findAllByGatheringIdAndParentId(gatheringId,
                                                                                                                findComment.getId());
            List<CommentDto> replyDtos = findReplies.stream().map(CommentEntity::toDto).collect(Collectors.toList());
            CommentDto commentDto = findComment.toParentDto(replyDtos);
            commentDtos.add(commentDto);
        }
        Page<CommentDto> commentDtoPage = new PageImpl<>(commentDtos,pageable,commentDtos.size());
        return commentDtoPage;
    }

    public CommentDto modifyComment(Long gatheringId, Long commentId, String email, CommentRequest commentRequest) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGathering = findPostById(gatheringId);
        CommentEntity findComment = commentRepository.findById(commentId)
                                                 .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND, "댓글을 찾을 수 없습니다."));
        if (!findUser.getUserName().equals(findComment.getUser().getUserName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자만 접근 가능합니다.");
        }
        findComment.update(commentRequest.getComment());
        CommentEntity savedComment = commentRepository.save(findComment);
        return savedComment.toDto();
    }

    public String deleteComment(Long gatheringId, Long commentId, String email) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGathering = findPostById(gatheringId);
        CommentEntity findComment = commentRepository.findById(commentId)
                                                     .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND, "댓글을 찾을 수 없습니다."));
        if (!findUser.getUserName().equals(findComment.getUser().getUserName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자만 접근 가능합니다.");
        }
        List<CommentEntity> findChildComments = commentRepository.findAllByGatheringIdAndParentId(gatheringId, commentId);
        for (CommentEntity findChildComment : findChildComments) {
            commentRepository.delete(findChildComment);
        }
        commentRepository.delete(findComment);
        return "삭제가 완료 되었습니다.";
    }

    public CommentDto writeReply(Long gatheringId, Long commentId, String email, CommentRequest commentRequest) {
        UserEntity findUser = findUserByEmail(email);
        GatheringEntity findGathering = findPostById(gatheringId);
        CommentEntity comment = CommentEntity.ofReply(commentRequest.getComment(), findGathering, findUser,commentId);
        CommentEntity savedComment = commentRepository.save(comment);
        CommentDto commentDto = savedComment.toDto();
        return commentDto;
    }
}