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
        log.info("--- APILoginFailHandler ---");

        Gson gson = new Gson();

        String json = gson.toJson(Map.of("error", "ERROR_LOGIN"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
