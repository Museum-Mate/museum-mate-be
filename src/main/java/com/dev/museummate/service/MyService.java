package com.dev.museummate.service;

import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.review.ReviewDto;
import com.dev.museummate.domain.dto.user.UserDto;
import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.GatheringEntity;
import com.dev.museummate.domain.entity.ParticipantEntity;
import com.dev.museummate.domain.entity.ReviewEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.AlarmRepository;
import com.dev.museummate.repository.BookmarkRepository;
import com.dev.museummate.repository.GatheringRepository;
import com.dev.museummate.repository.ParticipantRepository;
import com.dev.museummate.repository.ReviewRepository;
import com.dev.museummate.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<GatheringDto> getMyEnrolls(Pageable pageable, String email) {
        UserEntity user = findUserByEmail(email);
        Page<ParticipantEntity> enrollGatheringList = participantRepository.findAllByUserIdAndHostFlagAndApprove(user.getId(),
                                                                                                                 Boolean.FALSE,
                                                                                                                 Boolean.FALSE,
                                                                                                          pageable);
        List<GatheringDto> gatheringDtos = new ArrayList<>();
        for (ParticipantEntity participant : enrollGatheringList) {
            Optional<GatheringEntity> findGathering = gatheringRepository.findById(participant.getGathering().getId());
            GatheringDto gatheringDto = GatheringDto.toDto(findGathering.get(), participantRepository.countByGatheringIdAndApproveTrue(
                findGathering.get().getId()));
            gatheringDtos.add(gatheringDto);
        }

        return new PageImpl<>(gatheringDtos,pageable,gatheringDtos.size());
    }

    public Page<GatheringDto> getMyApprove(Pageable pageable, String email) {
        UserEntity user = findUserByEmail(email);
        Page<ParticipantEntity> enrollGatheringList = participantRepository.findAllByUserIdAndHostFlagAndApprove(user.getId(),
                                                                                                                 Boolean.FALSE,
                                                                                                                 Boolean.TRUE,
                                                                                                                 pageable);
        List<GatheringDto> gatheringDtos = new ArrayList<>();
        for (ParticipantEntity participant : enrollGatheringList) {
            Optional<GatheringEntity> findGathering = gatheringRepository.findById(participant.getGathering().getId());
            GatheringDto gatheringDto = GatheringDto.toDto(findGathering.get(), participantRepository.countByGatheringIdAndApproveTrue(
                findGathering.get().getId()));
            gatheringDtos.add(gatheringDto);
        }

        return new PageImpl<>(gatheringDtos,pageable,gatheringDtos.size());

    }

    public UserDto getMyInfo(String email) {
        UserEntity user = findUserByEmail(email);
        UserDto userDto = UserDto.toDto(user);
        return userDto;
    }
}
