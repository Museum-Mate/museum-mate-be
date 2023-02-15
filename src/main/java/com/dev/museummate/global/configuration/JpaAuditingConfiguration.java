package com.dev.museummate.global.configuration;

import com.dev.museummate.domain.entity.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

  // CreatedBy, ModifiedBy 자동 생성 설정 추가
  @Bean
  public AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

}
