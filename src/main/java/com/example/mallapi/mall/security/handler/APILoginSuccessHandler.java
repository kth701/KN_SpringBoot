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
        log.info(authentication);
        log.info("---------------");


        // 로그인 성공 -> JWT발급 ->클라이언트에 보내기
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        Map<String, Object> claims = memberDTO.getClaims();
        // JWT 발급
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60*24);
        // JWT 발급 받은 문자열 저장
        claims.put("accessToken", accessToken);
        claims.put("refreshToken",refreshToken);

        Gson gson = new Gson();
        String json = gson.toJson(claims);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(json);
        response.getWriter().flush();
        response.getWriter().close();

    }
}
