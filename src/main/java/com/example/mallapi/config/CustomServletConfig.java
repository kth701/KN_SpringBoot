package com.example.mallapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /*
            CORS(Cross-Origin Resource Sharing): 교차 출처 리소스 공유
                - 웹 브라우저가 보안상의 이유로 다른 출처(도메인, 프로토콜, 포트)의 리소스를 요청하는 것을 제한하는 SOP(Same-Origin Policy)를 우회하기 위한 메커니즘.
                - 프론트엔드(예: React, Vue) 서버와 백엔드 API 서버가 분리된 경우 주로 발생
                - Spring Boot에서 CORS를 설정하는 방법은 크게 두 가지가 있습니다.

             1. Spring Security의 CORS 설정 이용
             2. WebMvcConfigurer를 이용한 전역 설정 (현재 사용한 방식)
            - 이 방식은 컨트롤러 레벨의 CORS 설정보다 우선순위가 높으며, 애플리케이션 전반에 걸쳐 일관된 정책을 적용하기 용이.
         */

        registry.addMapping("/api/**") // 모든 경로에 대해 CORS 정책 적용
                .allowedOrigins("*") // 모든 출처(Origin)에서의 요청을 허용
                .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더를 허용
                .maxAge(300); // pre-flight 요청의 캐시 시간(초)
    }

    /*
        프로젝트 내부가 아닌 자신의 컴퓨터에서 파일을 찾는 경로로 uploadPath로 설정
            #  uploadPath변수에 1:1 맵핑될 이미지 url설정
            # "/images/**" => url에 "/images"로 시작하는 모든 경로에 대해 uploadPath에 설정한 폴더를 기준("file:///c:upload/")으로 파일을 읽어오도록 설정
            # 예) "/images/item"경로는 "file:///c:upload/item""프로토클 방식으로 url요청됨.
     */

    // 파일 업로드 실제 경로
    /*
         properties형식인 경우 최상위 설정
        @Value("${uploadPath}")
     */

    // yml형식인 경우 전체 경로 설정
    @Value("${com.example.mallapi.upload.uploadPath}")
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}
