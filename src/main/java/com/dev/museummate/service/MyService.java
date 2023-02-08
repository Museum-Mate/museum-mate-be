package com.dev.museummate.service;

import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.entity.*;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final GatheringRepository gatheringRepository;
    private final ParticipantRepository participantRepository;

    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_FOUND, String.format("%s님은 존재하지 않습니다.",email)));
    }

    public List<ExhibitionDto> getMyCalendar(String email){

        UserEntity user = findUserByEmail(email);

        List<ExhibitionEntity> exhibitionEntities = bookmarkRepository.findByUser(user).stream().map(
                bookmarkEntity -> bookmarkEntity.getExhibition()).collect(Collectors.toList());

        List<ExhibitionDto> exhibitionDtos =  exhibitionEntities.stream().map(exhibition -> ExhibitionDto.toDto(exhibition)).collect(Collectors.toList());
        return exhibitionDtos;
    }

    public Page<AlarmDto> getAlarms(Pageable pageable, String email) {
        UserEntity user = findUserByEmail(email);

        Page<AlarmEntity> alarmEntities = alarmRepository.findByUser(pageable, user);
        return alarmEntities.map(alarm -> AlarmDto.toDto(alarm));
    }

    public Page<ReviewDto> getMyReviews(Pageable pageable, String email) {
        UserEntity user = findUserByEmail(email);

        Page<ReviewEntity> reviewEntities = reviewRepository.findByUser(user, pageable);
        return reviewEntities.map(review -> ReviewDto.toDto(review));
    }

    public Page<GatheringDto> getMyGatherings(Pageable pageable, String email) {
        UserEntity user = findUserByEmail(email);
        Page<GatheringEntity> gatheringEntities = gatheringRepository.findByUser(user, pageable);
        Page<GatheringDto> gatheringDtos = gatheringEntities.map(gathering -> GatheringDto.toDto(gathering, participantRepository.countByGatheringIdAndApproveTrue(gathering.getId())));

        return gatheringDtos;
    }
}
