package com.example.mallapi.mall.security.filter;


import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {
    // 필터로 체크하지 않을 경로를 설정
    // true이면 필터처리 하지 않음, false 이면 필터처리
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("--> JWTCheckFilter path:"+path);

        // Prefilight요청은 체크하지 않음
        if (request.getMethod().equals("OPTIONS")) return true;
        //if (path.startsWith("/api/v1/todos/")) return true;

        if (path.startsWith("/api/member/")) return true;
        if (path.startsWith("/api/products/view")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("--> JWTCheckFilter start");

        String authHeaderStr = request.getHeader("Authorization");

        try {
            // 1. Authentication헤더 및 Bearer 확인 절차
            if (authHeaderStr == null && !authHeaderStr.startsWith("Bearer ")) {
                throw new CustomJWTException("Token is not available");
            }

            // 2. 토큰 추출 및 검증
            String token = authHeaderStr.substring(7);
            Map<String,Object> claims = JWTUtil.validateToken(token); // JWT토큰 검증
            log.info("--> JWT claims:"+claims);

            // 3. 토큰에 사용자 정보 추출 및 SecurityContext에 인증 정보 설정
            String email = (String) claims.get("email");
            String pwd = (String) claims.get("password");


        } catch (Exception e) {
           // JWT 관련 예외 발생시 에러 응답
            log.info("--> JWT CheckFilter exception: {}", e.getMessage());

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter out = response.getWriter();
            out.println(new Gson().toJson(Map.of("error", "ERROR_ACCESS_TOKEN")));
            out.flush();
            out.close();
        }

    }
}
