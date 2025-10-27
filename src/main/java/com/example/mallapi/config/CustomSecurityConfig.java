package com.example.mallapi.config;

import com.example.mallapi.mall.security.CustomUserDetailService;
import com.example.mallapi.mall.security.handler.APILoginFailHandler;
import com.example.mallapi.mall.security.handler.APILoginSuccessHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity// 메서드 단위로 접근권한 설정할 경우 사용하는 어노테이션
public class CustomSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security 로그인시 :  스프링 시큐리티 정보(principal.username,...) 와 DB정보 연동
    //  => DB의 정보를 가져와 스프링 시큐리티 세션 정보로 사용하는 것이 목적
    @Autowired
    CustomUserDetailService customUserDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("--- security configure : SecurityFilterChain ---");

        /* JWT처리관련 설정
        // CORS 설정
        http.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
        );


        // 세션 STATELESS 설정(무세션) => RestFul API 방식일 경우 적용
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        */

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());
        
        // security 로그인 폼 설정 => POST방식, username, password파리미터를 통해 로그인처리
        http.formLogin(config -> {
            // JWT 로그인 access token 인증하기
            config.loginPage("/api/member/login");// API서로 로그인 할 수 있게 설정
            config.successHandler(new APILoginSuccessHandler());
            config.failureHandler(new APILoginFailHandler());
        });

        /*
        // JWT 체크
        // 일반적으로 로그인 처리 이전에 JWT Check Filter먼저 처리(JWT인증처리할 것인지 여부 확인하는 절차)
        http.addFilterBefore(
                new JWTCheckFilter(), // 첫번째 인자는 두번째 인자 처리 이전에 실행
                UsernamePasswordAuthenticationFilter.class);// 두번째 인자
         */

        //  JWT방식이 아닌 요청 경로별 인가 설정
//        http.authorizeHttpRequests(authorize -> {
//            // 로그인 및 토큰 재발급 경로는 모두에게 허용
//            authorize.requestMatchers("/api/member/login", "/api/member/refresh","/api/member/kakao").permitAll();
//
//            // 그 외 모든 요청은 인증된 사용자만 접근 가능
//            authorize.anyRequest().authenticated();
//
//        });



        return http.build();
    }

    // JWT 및 RestFul API적용시 다른 도메인관련된 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        //configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000")); // 특정 도메인에 한 해서 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        //configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
