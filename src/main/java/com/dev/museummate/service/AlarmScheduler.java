package com.dev.museummate.service;

import com.dev.museummate.domain.AlarmType;
import com.dev.museummate.domain.entity.AlarmEntity;
import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.global.utils.MailUtils;
import com.dev.museummate.repository.AlarmRepository;
import com.dev.museummate.repository.BookmarkRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final BookmarkRepository bookmarkRepository;
    private final AlarmRepository alarmRepository;

    @Scheduled(cron = "0 26 6 * * *")
    @Transactional
    public void makeAlarms(){

        //북마크 돌면서 1일전꺼 알람 발생
        bookmarkAlarms(AlarmType.DAY_BEFORE_END);

        //북마크 돌면서 7일전꺼 알람 발생
        bookmarkAlarms(AlarmType.WEEK_BEFORE_END);
    }

    private void bookmarkAlarms(AlarmType alarmType) {
        String date = LocalDateTime.now().plusDays(alarmType.getLeftDate()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String dateAnother = LocalDateTime.now().plusDays(alarmType.getLeftDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<BookmarkEntity> bookmarkEntityList = bookmarkRepository.findByExhibition_EndAt(dateAnother);
        List<BookmarkEntity> bookmarkEntityList2 = bookmarkRepository.findByExhibition_EndAtStartsWith(date);
        bookmarkEntityList.addAll(bookmarkEntityList2);

        for (BookmarkEntity bookmark: bookmarkEntityList) {

            AlarmEntity alarm = AlarmEntity.createAlarm(bookmark.getUser(), bookmark.getExhibition(), alarmType.getAlarmMessage());
            alarmRepository.save(alarm);

            MailUtils.bookmarkMailSend(bookmark.getUser().getEmail(), bookmark.getUser().getUserName(), bookmark.getExhibition().getName(),
                                       alarmType.getLeftDate());
        }
    }
}

