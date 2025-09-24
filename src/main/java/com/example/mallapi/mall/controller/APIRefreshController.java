package com.example.mallapi.mall.controller;

import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class APIRefreshController {

    @PostMapping("/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Refresh-Token") String refreshToken) {

        // 1. Check for tokens
        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomJWTException("NULL_ACCESS");
        }
        String accessToken = authHeader.substring(7);

        // 2. Check if Access Token is still valid
        // NOTE: This logic assumes JWTUtil.validateToken() throws an exception for expired tokens.
        try {
            JWTUtil.validateToken(accessToken);
            // If no exception is thrown, the token is still valid. Return original tokens.
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        } catch (CustomJWTException e) {
            // This block is executed if the access token is invalid, which is expected if it's expired.
            log.info("Access Token has expired, proceeding with refresh...");
        }

        // 3. Validate the Refresh Token
        Map<String, Object> claims;
        try {
            claims = JWTUtil.validateToken(refreshToken);
        } catch (Exception e) {
            // If refresh token is invalid, throw an exception.
            log.error("Refresh Token is invalid", e);
            throw new CustomJWTException("INVALID_REFRESH");
        }

        // 4. Generate new Access Token from refresh token's claims
        String newAccessToken = JWTUtil.generateToken(claims, 10); // New access token with 10 min validity

        // 5. Check if Refresh Token needs to be reissued
        long exp = ((Number) claims.get("exp")).longValue();
        long now = System.currentTimeMillis() / 1000;
        long gap = exp - now;

        String newRefreshToken = refreshToken;
        // If less than 7 days are left, issue a new refresh token
        if (gap < (60 * 60 * 24 * 7)) {
            log.info("Issuing new refresh token...");
            newRefreshToken = JWTUtil.generateToken(claims, 30 * 24 * 60); // New refresh token with 30 days validity
        }

        // 6. Return the new tokens
        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }
}
