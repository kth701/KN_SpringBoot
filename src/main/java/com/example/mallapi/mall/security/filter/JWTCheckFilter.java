package com.example.mallapi.mall.security.filter;


import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {
    // JWT Check Filter로 체크하지 않을 경로를 설정
    // true이면 JWT Check Filter처리 하지 않음, false 이면 JWT Check Filter처리
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("--> JWTCheckFilter path:"+path);

        // APIController 요청 경로 설정
        // Prefilight요청은 Security JWT Check Filter 체크하지 않음
        if (request.getMethod().equals("OPTIONS")) return true;
        //if (path.startsWith("/api/v1/todos/")) return true;

        if (path.startsWith("/api/member/")) return true;
        //if (path.startsWith("/api/member/refresh")) return true;
        if (path.startsWith("/api/products/view")) return true;


        // TodoController 요청 경로 설정
        if (path.startsWith("/todos/")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("--> JWTCheckFilter start");

        String authHeaderStr = request.getHeader("Authorization");

        try {
            // 1. Authentication헤더 및 Bearer 확인 절차
            if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
                throw new CustomJWTException("Token is not available");
            }

            // 2. 토큰 추출 및 검증
            String token = authHeaderStr.substring(7);
            Map<String,Object> claims = JWTUtil.validateToken(token); // JWT토큰 검증
            log.info("--> JWT claims:"+claims);

            // 3. 토큰에 사용자 정보 추출 및 SecurityContext에 인증 정보 설정
            String email = (String) claims.get("email");
            String pwd = ""; // 패스워드는 JWT에 포함되지 않으므로 임의의 값을 사용


            List<String> roleNames = (List<String>) claims.get("roleNames");
            if (roleNames == null) {
                roleNames = new ArrayList<>();
            }

            MemberDTO memberDTO = new MemberDTO(
                    email,
                    pwd,
                    (String)claims.get("nickname"),
                    (Boolean) claims.get("social"),
                    (Boolean) claims.get("del"),
                    roleNames,
                    (LocalDateTime) claims.get("regTime") // 가입 날짜
            );
            log.info("--> JWT claims:"+memberDTO);

            // 4. Security에 인증 정보 등록
            //  AuthenticationToken을 생성하여 사용자 정보 => principal =>
            //  비밀번호(credentials), 권한(authorities)을 설정
            //  이 토큰은 현재 요청이 인증되었음 Spring Security에 알리는 역할
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            memberDTO, // 사용자 정보(principal):email,pw,...
                            pwd, // 실제 인증에 사용되지않음(credentials)
                            memberDTO.getAuthorities()// 권한정보: MemberDTO에서 권한 목록 가져옴
                    );

            // SecurityContextHolder에 인증정보를 설정하여, 현재 스레드(요청)에서 인증된 사용자로 처리됨.
            // 이후 필터나 컨트롤러에서 @PreAuthorize와 같은 어노테이션이 동작
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);

            // 다음 필터로 전달
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // JWT 관련 예외 발생시 에러 응답
            log.error("--> JWT CheckFilter exception: {}", e.getMessage());

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter out = response.getWriter();
            out.println(new Gson().toJson(Map.of("error", "ERROR_ACCESS_TOKEN")));
            out.flush();
            out.close();
        }

    }
}
