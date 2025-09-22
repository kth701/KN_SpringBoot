package com.example.mallapi.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTUtil {

    private static final String key = "MySecretKeyForJsonWebTokenIsVeryLongAndSecure12345";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    // JWT문자열 생성
    public static String generateToken(Map<String, Object> valueMap, int min) {
        return Jwts.builder()
                .header()// 헤더 설정
                .add("typ", "JWT")// 헤드 타입
                .and()
                .claims(valueMap)//페이로드(clams)에 전달된 맵 데이터 설절
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))//발행 시간
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))// 만료 시간
                .signWith(SECRET_KEY)// 지정된 비밀키와 기본 알고리즘으로 서명(HS256)
                .compact();// 모든 설정 바탕으로 JWT문자열 생하고 직렬화
    }

    // JWT문자열 검증
    public static Map<String, Object> validateToken(String token) throws JwtException {
        Map<String, Object> claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET_KEY) // 서명 검증에 사용할 비밀키 설정
                    .build()
                    .parseSignedClaims(token)// 토큰 문자열을 파싱하고 서명을 검증, 실패 시 에러
                    .getPayload();// 검증된 토큰의 페이로드(claims)반환
        } catch (ExpiredJwtException e) {
            log.error("Token has expired: {}", e.getMessage());
            throw new JwtException("EXPIRED");
        } catch (MalformedJwtException e) {
            log.error("Malformed Token: {}", e.getMessage());
            throw new JwtException("MALFORMED");
        } catch (SignatureException e) {
            log.error("Invalid Signature: {}", e.getMessage());
            throw new JwtException("SIGNATURE");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported Token: {}", e.getMessage());
            throw new JwtException("UNSUPPORTED");
        } catch (IllegalArgumentException e) {
            log.error("Invalid Token: {}", e.getMessage());
            throw new JwtException("ILLEGAL");
        }
        return claims;
    }
}
