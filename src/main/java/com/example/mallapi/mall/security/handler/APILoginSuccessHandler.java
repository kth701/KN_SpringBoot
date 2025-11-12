package com.example.mallapi.mall.security.handler;

import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--- APILoginSuccessHandler ---");
        log.info("--> Authentication: {}", authentication);
        log.info("---------------");
        log.info("=>1. authentication.toString(): {} ",authentication.toString());
        log.info("=>2. authentication.username(): {}",authentication.getName());
        log.info("=>3. authentication.getAuthorities(): {}",authentication.getAuthorities());
        log.info("=>4. authentication.getPrincipal(): {}",authentication.getPrincipal());
        log.info("---------------");
        MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal();
        log.info("-> MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal()");
        log.info("->5.  memberDTO.getNickname(): "+memberDTO.getNickname());
        log.info( "->6.  ((MemberDTO)authentication.getPrincipal()).getNickname(): "+((MemberDTO)authentication.getPrincipal()).getNickname());
        log.info("---------------");

        // 서버 페이지에 로그인 성공시 메시지 전달하기 처리하고, json형식 에러 메시지 데이터 클라이언트에게 보내기
        // -> 정상 작동 ,. jwt 정상 처리한 후  test필요)

        // 로그인 성공후 후처리 작업한 뒤 마지막 보여질 View
        response.sendRedirect("/");

        // ------------------------------------------------------------------------------------- //
        // 로그인 성공시 JWT 발급하기  ->클라이언트에 보내기
        // ------------------------------------------------------------------------------------- //
        Map<String, Object> claims = memberDTO.getClaims();
        // JWT 발급
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60*24);
        // JWT 발급 받은 문자열 저장
        claims.put("accessToken", accessToken);
        claims.put("refreshToken",refreshToken);

        // 주의 : regTime 날짜 호환에러 -> 'jackson-datatype-jsr310' 라이브러리 추가 설치
        // implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1' // 사용 중인 Jackson 버전에 맞춰 조정
        //대부분의 Spring Boot 환경에서는 위의 의존성을 추가하기만 하면 Spring Boot의 자동 설정이 이 모듈을 
        // Jackson ObjectMapper에 자동으로 등록해 줌
        // 'Cannot serialize JWS Payload to JSON. Cause: Unable to serialize object of type io.jsonwebtoken.impl.DefaultClaims: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (or disable `MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES`) (through reference chain: io.jsonwebtoken.impl.DefaultClaims["regTime"])' 에러 메시지

        // user1@test.com,... 계정 정상 처리,  test1@test.com 에러 메시지 발생 ????
        Gson gson = new Gson();
        String json = gson.toJson(claims);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(json);
        response.getWriter().flush();
        response.getWriter().close();

        log.info("---------- 로그인 성공시 JWT 발급하기  ->클라이언트에 보내기--------------");
        log.info("---------- JWT :API LoginSuccessHandler : json message -> {}", json);
        // ------------------------------------------------------------------------------------- //




    }
}
