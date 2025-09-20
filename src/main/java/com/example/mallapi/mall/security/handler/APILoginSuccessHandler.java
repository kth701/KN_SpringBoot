package com.example.mallapi.mall.security.handler;

import com.example.mallapi.mall.dto.MemberDTO;
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

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        Map<String, Object> claims = memberDTO.getClaims();

        claims.put("accessToken", "");
        claims.put("refreshToken","");

        Gson gson = new Gson();
        String json = gson.toJson(claims);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(json);
        response.getWriter().flush();
        response.getWriter().close();

    }
}
