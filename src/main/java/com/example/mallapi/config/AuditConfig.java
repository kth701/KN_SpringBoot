package com.example.mallapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// ---------------------------------  //
// Auditiong을 이용한 공통 속성 등록
// ---------------------------------  //

// 엔티티가 저장 또는 수정될 때 자동으로 등록일, 수정일, 등록자, 수정자를 입력해주는 기능
@Configuration
@EnableJpaAuditing// JPA의 Auditing 기능 활성화 설정
public class AuditConfig {

    // AuditorAwareImpl을 빈으로 등록
    @Bean
    public AuditorAware<String> auditorAware() {

        // 등록자와 수정자를 처리해주는 AuditorAwareImpl를 빈으로 등록
        return new AuditorAwareImpl();
    }

}
