package com.example.mallapi.mall.controller;

import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class APIRefreshController {

    @PostMapping("/refresh")
    public Map<String, Object> refresh(
            @RequestHeader(value="Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        log.info("--> access token with Bearer: {} " , authHeader);

        // 1. refreshToken 유무 체크
        if (refreshToken == null) { // refresh token없으면 예외 발생
            throw new CustomJWTException("NULL_REFRESH");
        }

        // 2. Authorization헤더 형식 확인 : 헤더가 존재하고, "Bearer 로 시작하는지 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomJWTException("NULL_ACCESS");// access token없으면 예외발생
        }

        String accessToken = authHeader.substring(7);

        // 3. access token이 아직 유효한지 확인
        // NOTE: JWTUtil.validateToken()이 만료된 토큰에 대해 예외 처리
        try {
            // 클라이언트로 받은 access token이 서버가 생성한 access token인지 판별
            JWTUtil.validateToken(accessToken);
            // 예외가 발생하지 않으면 토큰이 아직 유효하므로 기존 토큰을 그대로 반환
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        } catch (CustomJWTException e) {
            // 유효하지 않으면 경우(만료된 경우) 예외발생
            log.info("Access Token이 만료되어 갱신 절차를 진행합니다....");
        }

        // 4. Refresh Token 유효성 검사
        Map<String, Object> claims;
        try {
            claims = JWTUtil.validateToken(refreshToken);
            log.info("--> refresh token ... claims: {}" , claims);

        } catch (Exception e) {
            // refresh token 유효하지 않으면 예외 발생
            log.error("Refresh Token is invalid", e);
            throw new CustomJWTException("INVALID_REFRESH");
        }

        // 5. refresh token의 클래임으로 new access token
        String newAccessToken = JWTUtil.generateToken(claims, 10); // New access token with 10 min validity

        // 6. Refresh Token의 유효시간 checkTime()메서드를 통해 1시간 미만 여부 체크해서
        //     1시간 미만이면 new refresh token생성
        String newRefreshToken = checkTime( (long)claims.get("exp"))
                // refresh token이 1시간 미만이면 new refresh token생성
                ? JWTUtil.generateToken(claims, 60*24)
                // 1시간 이상이면 기존 refresh token 그대로 사용
                : refreshToken;
        log.info("--> refresh token 정상처리");
        log.info("--> /api/member/refresh");
        log.info("--> newAccessToken : {}", newAccessToken);
        log.info("--> newRefreshToken : {}", newRefreshToken);



        // 7. Return the new tokens
        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }


    // Refresh Token의 유효시간이 1시간 미만으로 남았다면 처리하는 메서드
    //  => exp객체는 Long타입이며 초(seconds)단위로 1000을 곱해 밀리초로 변환
    public boolean checkTime(long exp){
        // 1. JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long)exp * 1000L);
        // 2. 토큰 만료 시간과 현재 시간의 차이를 밀리초 단위로 계산
        long gap = expDate.getTime() - System.currentTimeMillis();
        // 3. 밀리초를 분
        long leftMin = gap/(1000*60);
        // 4. 1시간(3600초) 미만인지 확인
        return leftMin < 60;
        // 유효기간이 1시간 미만 true, 1시간 이상이면 false
    }
}