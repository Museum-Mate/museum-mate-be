package com.dev.museummate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.dev.museummate.domain.dto.user.UserDto;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.ParticipantEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.fixture.UserEntityFixture;
import com.dev.museummate.repository.AlarmRepository;
import com.dev.museummate.repository.BookmarkRepository;
import com.dev.museummate.repository.CommentRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.GatheringRepository;
import com.dev.museummate.repository.ParticipantRepository;
import com.dev.museummate.repository.ReviewRepository;
import com.dev.museummate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

class MyServiceTest {

    MyService myService;

    BookmarkRepository bookmarkRepository = mock(BookmarkRepository.class);
    GatheringRepository gatheringRepository = mock(GatheringRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ParticipantRepository participantRepository = mock(ParticipantRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);
    ReviewRepository reviewRepository = mock(ReviewRepository.class);

    @BeforeEach
    public void setUp() {
        myService = new MyService(userRepository, bookmarkRepository,alarmRepository,reviewRepository,gatheringRepository,participantRepository);
    }

    @Test
    @DisplayName("내 정보 조회 - 성공")
    public void getMyInfo_success() {

        UserEntity user = UserEntity
            .builder()
            .userName("엄복동")
            .email("email@naver.com")
            .address("대한민국")
            .phoneNumber("01012341234")
            .build();

        given(userRepository.findByEmail("email@naver.com"))
            .willReturn(Optional.of(user));

        UserDto myInfo = myService.getMyInfo("email@naver.com");

        assertEquals(user.getUserName(),myInfo.getUserName());
        assertEquals(user.getEmail(),myInfo.getEmail());
        assertEquals(user.getAddress(),myInfo.getAddress());
        assertEquals(user.getPhoneNumber(),myInfo.getPhoneNumber());
    }

    @Test
    @DisplayName("내 정보 조회 - 유저 조회 실패")
    public void getMyInfo_fail() {

        UserEntity user = UserEntity
            .builder()
            .userName("엄복동")
            .email("email@naver.com")
            .address("대한민국")
            .phoneNumber("01012341234")
            .build();

        given(userRepository.findByEmail("email@naver.com"))
            .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, "조회 불가"));

        try {
            UserDto myInfo = myService.getMyInfo("email@naver.com");
        } catch (Exception e) {
            assertEquals("email not found: 조회 불가",e.getMessage());
        }

    }

}