package com.example.mallapi.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
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

    // JWT 생성에 사용할 비밀키. 외부에 노출되지 않도록 주의해야 합니다.
    private static final String key = "MySecretKeyForJsonWebTokenIsVeryLongAndSecure12345";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

    /**
     * JWT 토큰을 생성
     *
     * @param valueMap 페이로드(payload)에 포함될 클레임(claim) 맵
     * @param min      토큰의 유효 기간 (분 단위)
     * @return 생성된 JWT 문자열
     * dataMap문자열: MemberDTO클래스에 있는 getClaims() 구현시 반환된 dataMap
     * -> 로그인 성공시(APILoginSuccessHandler클래스에서 JWT발급) 추출한 정보(MemberDTO클래스 getClaims()구현시 dataMap객체 생성 ) -> JWT문자열 생성 하기 위한 dataMap
     */
    public static String generateToken(Map<String, Object> valueMap, int min) {
        return Jwts.builder()
                .header() // 헤더(Header) 설정
                .add("typ", "JWT") // 토큰 타입: JWT
                .and()
                .claims(valueMap) // 페이로드(claims)에 전달된 맵 데이터 설정(dataMap)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발행 시간(iat)
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 만료 시간(exp)
                .signWith(SECRET_KEY) // 서명(Signature): 지정된 비밀키와 HS256 알고리즘으로 서명
                .compact(); // 모든 설정을 바탕으로 JWT 문자열 생성 및 직렬화
    }

    /**
     * JWT 토큰을 검증하고 페이로드(claims)를 반환합니다.
     * <p>
     * 토큰 검증 과정에서 발생할 수 있는 다양한 예외를 처리하고,
     * 각각의 경우에 맞는 {@link CustomJWTException}을 발생시켜 일관된 예외 처리를 제공합니다.
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 토큰의 페이로드(claims) 맵
     * @throws CustomJWTException 토큰이 유효하지 않거나 검증 과정에서 오류가 발생한 경우
     */
    public static Map<String, Object> validateToken(String token) throws CustomJWTException {
        Map<String, Object> claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET_KEY) // 1. 서명 검증을 위한 비밀키 설정
                    .build()
                    .parseSignedClaims(token) // 2. 토큰 파싱 및 서명 검증. 실패 시 예외 발생
                    .getPayload(); // 3. 검증 성공 시 페이로드(claims) 반환
        } catch (MalformedJwtException e) {
            // 예외 처리: JWT 구조가 잘못된 경우
            log.error("토큰 형식이 잘못되었습니다 (Malformed): {}", e.getMessage());
            throw new CustomJWTException("MALFORMED");
        } catch (ExpiredJwtException e) {
            // 예외 처리: 토큰 유효 기간 만료
            log.error("토큰이 만료되었습니다 (Expired): {}", e.getMessage());
            throw new CustomJWTException("EXPIRED");
        } catch (SignatureException e) {
            // 예외 처리: 서명이 유효하지 않은 경우
            log.error("서명이 유효하지 않습니다 (Invalid Signature): {}", e.getMessage());
            throw new CustomJWTException("SIGNATURE");
        } catch (UnsupportedJwtException e) {
            // 예외 처리: 지원되지 않는 JWT 형식인 경우
            log.error("지원되지 않는 토큰입니다 (Unsupported): {}", e.getMessage());
            throw new CustomJWTException("UNSUPPORTED");
        } catch (InvalidClaimException e) {
            // 예외 처리: 클레임이 유효하지 않은 경우
            log.error("클레임이 유효하지 않습니다 (Invalid Claim): {}", e.getMessage());
            throw new CustomJWTException("INVALID_CLAIM");
        } catch (JwtException e) {
            // 예외 처리: 기타 JWT 관련 예외
            log.error("JWT 처리 중 오류가 발생했습니다: {}", e.getMessage());
            throw new CustomJWTException("JWT_ERROR");
        } catch (IllegalArgumentException e) {
            // 예외 처리: 인자가 부적절한 경우 (e.g., 토큰이 null 또는 비어있음)
            log.error("부적절한 토큰입니다 (Illegal Argument): {}", e.getMessage());
            throw new CustomJWTException("ILLEGAL");
        } catch (Exception e) {
            // 예외 처리: 그 외 모든 알 수 없는 오류
            log.error("알 수 없는 토큰 오류가 발생했습니다: {}", e.getMessage());
            throw new CustomJWTException("UNKNOWN");
        }
        return claims;
    }
}
