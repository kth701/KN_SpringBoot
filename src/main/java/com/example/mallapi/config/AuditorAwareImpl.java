package com.example.mallapi.config;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
// ---------------------------------  //
// Auditiong을 이용한 공통 속성 공통화
// ---------------------------------  //

// 엔티티가 저장 또는 수정될 때 자동으로 등록일, 수정일, 등록자, 수정자를 입력해주는 기능

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public java.util.Optional<String> getCurrentAuditor() {

        // 현재 로그인한 사용자의 정보를 조회하여 사용자의 이름 등록자, 수정자로 지정
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = "";

        // Security 인증 과정이 정상적으로 처리되었을 때
        if (authentication != null) {
            username = authentication.getName(); // 현재 로그인 한 사용자의 정보를 조회
        }

        return Optional.of(username);
    }

}
