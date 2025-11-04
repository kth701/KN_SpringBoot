package com.example.mallapi.mall.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class APILoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("---------- APILoginFailHandler  구현 ----------");

        // 서버 페이지에 로그인 실패시 메시지 전달하기 처리하고, json형식 에러 메시지 데이터 클라이언트에게 보내기
        // -> 정상 작동 ,. jwt 정상 처리한 후  test필요)
        response.sendRedirect("/members/login/error");

        // ------------------------------------------------------------------------------------- //
        // 로그인 실패시 JWT 발급하기  ->클라이언트에 보내기
        // ------------------------------------------------------------------------------------- //
        /*  */
        Gson gson = new Gson();

        String json = gson.toJson(Map.of("error", "ERROR_LOGIN"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(json);
        response.getWriter().flush();
        response.getWriter().close();
        log.info("---------- 로그인 실패시 JWT 발급하기  ->클라이언트에 보내기--------------");
        log.info("---------- JWT :API LoginFailHandler : json message -> {}", json);

        // ------------------------------------------------------------------------------------- //



    }
}
