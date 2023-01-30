package com.dev.museummate.domain.entity;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {

    // 인증 객체 정보 생성
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String userName = "";

    // 검증
    if (authentication != null ) {
      userName = authentication.getName();
    }

    return Optional.of(userName);
  }
}
