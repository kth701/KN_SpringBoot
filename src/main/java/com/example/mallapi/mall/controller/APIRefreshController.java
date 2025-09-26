package com.example.mallapi.mall.controller;

import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/member")
public class APIRefreshController {

    // GET 방식 요청을 방지하고, 파라미터를 명확히 하기 위해 @PostMapping과 @RequestParam을 사용합니다.
    @PostMapping("/refresh")
    public Map<String, Object> refresh(
            // 매개변수 설정에 맞게 postman 매개변수 설정 할 것
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        log.info("Access Token With Bearer....{}",authHeader);

        // 1. Refresh Token 존재 여부 확인: 요청에 Refresh Token이 포함되지 않았다면 예외를 발생
        if (refreshToken == null){
            throw new CustomJWTException("Refresh Token is not available");
        }

        // 2. Authorization 헤더 형식 확인: 헤더가 존재하고, "Bearer " 접두사를 포함할 수 있는 최소 길이를 가졌는지 검사.
        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("Invalid Access Token");
        }

        String accessToken = authHeader.substring(7);

        // Access Token이 만료되지 않았다면
//        if (!JWTUtil.checkExpiredToken(accessToken)) {
//            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
//        }

        // 3. 액세스 토큰이 아직 유효한지 확인
        // 참고: 이 로직은 JWTUtil.validateToken()이 만료된 토큰에 대해 예외를 발생
        try {
            JWTUtil.validateToken(accessToken);
            // 예외가 발생하지 않으면 토큰이 아직 유효하므로 기존 토큰을 반환
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        } catch (CustomJWTException e) {
            // 이 블록은 액세스 토큰이 유효하지 않은 경우(만료된 경우) 실행
            log.info("액세스 토큰이 만료되어 갱신 절차를 진행합니다...");
        }


        // Refresh Token 검증
        Map<String, Object> claims = null;
        try {
            claims = JWTUtil.validateToken(refreshToken);
        } catch (CustomJWTException e) {
            throw new CustomJWTException("Refresh Token Expired");
        }

        log.info("Refresh Token claims: " + claims);

        // 6. 검증된 Refresh Token의 정보를 바탕으로 새로운 Access Token 생성
        // 기존 Refresh Token에 있던 사용자 정보(claims)를 그대로 사용하여 새로운 Access Token을 발급
        String newAccessToken = JWTUtil.generateToken(claims, 10); // 10분 유효

        // 7. Refresh Token 만료 시간 확인 및 재발급 (Refresh Token Rotation)
        // 현재 Refresh Token의 만료 시간이 1시간 미만으로 남았다면, 새로운 Refresh Token을 생성하여 보안을 강화
        // 그렇지 않다면, 기존 Refresh Token을 그대로 반환

        /*
        // 주의: 대부분의 JWT 라이브러리는 이()exp (만료 시간) 클레임) 값을 데이터 손실 없이 안전하게 처리하기 위해 Long 타입으로 전환
        // 1-1.
        // long exp = ((Number) claims.get("exp")).longValue();
        // String newRefreshToken =
        //     checkTime(exp) == true
        //         ? JWTUtil.generateToken(claims, 60*24)
        //         : refreshToken;

         */

        //2-1 -> RefreshToken을 checkTime()메서드를 남은 유효시간 체크 new refresh token생성 여부 결저
        String newRefreshToken =  checkTime(((Number) claims.get("exp")).longValue())
                ? JWTUtil.generateToken(claims, 60*24)      // 새 리프레시 토큰 (유효 기간 1일)
                : refreshToken;

        log.info("--> Refresh Token 생성하기 정상 처리 ");
        log.info("--> 요청URL: '/react/member/refresh' ");
        log.info("--> newAccessToken:{} " ,newAccessToken);
        log.info("--> newRefreshToken: {}" ,newRefreshToken);

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // RefreshToken 시간이 1시간 미만으로 남았다면 처리하는 메서드 :
    // exp는 Long타입이며 초(seconds) 단위이므로 1000을 곱해 밀리초로 변환
    private boolean checkTime(long exp){

        // JWT exp를 날짜로 변환
        // 1-2
        // java.util.Date expDate = new java.util.Date(exp * 1000L);

        // 2-2
        java.util.Date expDate = new java.util.Date((long)exp * 1000);
        // 토큰 만료 시간과 현재 시간의 차이를 밀리초 단위로 계산
        long gap = expDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60); // 밀리초를 분
        return leftMin < (60 ); // 1시간(3600초) 미만인지 확인
    }





}


/*
 *
 *

오류 원인 분석
    java.lang.ClassCastException: class java.lang.Long cannot be cast to class java.lang.Integer 오류는
    이름 그대로 Long 타입의 객체를 Integer 타입으로 강제로 형변환하려고 할 때 발생

    문제의 원인은 JWT(JSON Web Token)의 exp (만료 시간) 클레임
    exp 클레임은 1970년 1월 1일부터의 시간을 초(second)로 나타내는 숫자(Unix-timestamp)
    이 값은 Integer가 저장할 수 있는 최댓값(약 21억, 2038년)을 쉽게 초과할 수 있음
    따라서, 대부분의 JWT 라이브러리는 이 값을 데이터 손실 없이 안전하게 처리하기 위해 Long 타입으로 전환







 * @RequestHeader 수정 (오류 해결)

기존: @RequestHeader("Authorization")
변경: @RequestHeader(value = "Authorization", required = false)
이유: required = false 속성을 추가하여 Authorization 헤더를 선택적 설정
      이제 헤더가 없어도 MissingRequestHeaderException이 발생하지 않으며, 대신 authHeader 변수에 null이 전달됩니다. 컨트롤러 내의 if (authHeader == null ...) 로직이 이 null 값을 정상적으로 처리하므로
      애플리케이션이 더 안정적으로 동작


@PostMapping 사용

기존: @RequestMapping("/refresh")
변경: @PostMapping("/refresh")
이유: 토큰 재발급은 서버의 데이터를 변경하는 작업은 아니지만, 민감한 정보를 다루고 멱등성(idempotent)을 보장하지 않으므로
        GET 메서드보다 POST 메서드를 사용하는 것이 REST API 설계 원칙에 더 부합


@RequestParam 명시

기존: String refreshToken
변경: @RequestParam("refreshToken") String refreshToken
이유: refreshToken이 요청의 파라미터(form-data 또는 query string)로 전달된다는 것을 명확히 하여 코드의 가독성을 높임

Access Token 유효기간 수정

기존: JWTUtil.generateToken(claims, 1)
변경: JWTUtil.generateToken(claims, 10)
이유: 코드 주석에는 // 10분 유효라고 되어 있으나 실제 코드는 1분으로 설정되어 있어, 주석과 동작을 일치시켜 잠재적인 버그를 수정

 */



