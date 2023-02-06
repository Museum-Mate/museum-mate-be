package com.dev.museummate.service;

import com.dev.museummate.domain.dto.alarm.AlarmDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.AlarmRepository;
import com.dev.museummate.repository.BookmarkRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AlarmRepository alarmRepository;

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
}
