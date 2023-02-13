package com.dev.museummate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.ParticipantEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.CommentRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.GatheringRepository;
import com.dev.museummate.repository.ParticipantRepository;
import com.dev.museummate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GatheringServiceTest {

    GatheringService gatheringService;

    GatheringRepository gatheringRepository = mock(GatheringRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ExhibitionRepository exhibitionRepository = mock(ExhibitionRepository.class);
    ParticipantRepository participantRepository = mock(ParticipantRepository.class);
    CommentRepository commentRepository = mock(CommentRepository.class);

    @BeforeEach
    public void setUp() {
        gatheringService = new GatheringService(gatheringRepository, userRepository, exhibitionRepository, participantRepository, commentRepository);
    }

    @Test
    @DisplayName("참가 신청 - 성공")
    public void post_success() {

        ParticipantEntity mockParticipant = mock(ParticipantEntity.class);
        UserEntity user = mock(UserEntity.class);
        GatheringEntity gathering = mock(GatheringEntity.class);

        given(userRepository.findByEmail("email@naver.com"))
            .willReturn(Optional.of(user));

        given(gatheringRepository.findById(1L))
            .willReturn(Optional.of(gathering));

        given(participantRepository.save(any()))
            .willReturn(mockParticipant);

        String enroll = gatheringService.enroll(1L, "email@naver.com");
        assertEquals(enroll,"신청이 완료 되었습니다.");
    }

    @Test
    @DisplayName("참가 신청 - 실패#1 이메일 조회 불가")
    public void post_fail_1() {

        ParticipantEntity mockParticipant = mock(ParticipantEntity.class);
        UserEntity user = mock(UserEntity.class);
        GatheringEntity gathering = mock(GatheringEntity.class);

        given(userRepository.findByEmail("email@naver.com"))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND));

        try {
            String enroll = gatheringService.enroll(1L, "email@naver.com");
        } catch (Exception e) {
            assertEquals(e.getMessage(),"email not found");
        }
    }

    @Test
    @DisplayName("참가 신청 - 실패#2 모집 글 조회 불가")
    public void post_fail_2() {

        ParticipantEntity mockParticipant = mock(ParticipantEntity.class);
        UserEntity user = mock(UserEntity.class);
        GatheringEntity gathering = mock(GatheringEntity.class);

        given(userRepository.findByEmail("email@naver.com"))
            .willReturn(Optional.of(user));

        given(gatheringRepository.findById(1L))
            .willThrow(new AppException(ErrorCode.GATHERING_POST_NOT_FOUND));

        try {
            String enroll = gatheringService.enroll(1L, "email@naver.com");
        } catch (Exception e) {
            assertEquals(e.getMessage(),"Gathering post not found");
        }
    }

    @Test
    @DisplayName("참가 신청 - 실패#3 이미 신청한 회원이 중복 신청할 경우")
    public void post_fail_3() {

        ParticipantEntity mockParticipant = mock(ParticipantEntity.class);
        UserEntity user = mock(UserEntity.class);
        GatheringEntity gathering = mock(GatheringEntity.class);

        given(userRepository.findByEmail("email@naver.com"))
            .willReturn(Optional.of(user));

        given(gatheringRepository.findById(1L))
            .willReturn(Optional.of(gathering));

        given(participantRepository.findByUserIdAndGatheringId(1L, 1L))
            .willThrow(new AppException(ErrorCode.DUPLICATED_ENROLL, ""));

        try {
            String enroll = gatheringService.enroll(1L, "email@naver.com");
        } catch (Exception e) {
            assertEquals(e.getMessage(),"User is Duplicate");
        }

    }


}