package com.dev.museummate.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AlarmType {
    WEEK_BEFORE_END(7,"전시 종료 7일 전입니다."),
    DAY_BEFORE_END(1, "전시 종료 1일 전입니다.")
    ;
    private Integer leftDate;
    private String alarmMessage;
}
