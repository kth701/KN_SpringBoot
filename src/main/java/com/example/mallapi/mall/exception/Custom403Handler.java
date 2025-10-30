package com.example.mallapi.mall.exception;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/*
    HTTP 403 Forbidden 클라이언트 오류 상태 응답 코드는 서버에 요청이 전달되었지만, 권한 때문에 거절되었다는 것을 의미
    - 서버는 이미 클라이언트 신원을 확인 되어 있으며, 문제는 그들의 신원이 아니라 권한에 문제가 있어 거절.
 */
@Log4j2
public class Custom403Handler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("---- AccessDeniedHandler(신원확인된 상태에서 권한 문제 발생하여 처리하는 메서드) ----");

        response.setStatus(HttpStatus.FORBIDDEN.value());

        // JSON 요청 확인
        String contentType = request.getHeader("Content-Type");
        //  null이면 예외발생하므로 null인 경우 false로 처리
        boolean jsonRequest = contentType != null && contentType.startsWith("application/json");
        log.info("isJSON: "+jsonRequest);

        // 일반 request요청
        if (!jsonRequest){
            response.sendRedirect("/members/login?error=ACC ESS_DENIED");
        }

    }
}
/*
403 Forbidden 상태 코드는 서버가 요청을 이해하고 클라이언트의 신원을 인식했지만, 요청을 이행하기를 거부하고 있음을 나타냄
클라이언트는 수정 없이 요청을 반복해서는 안되며, 단순히 다시 시도하는 것은 의미가 없음.

 401 Unauthorized와 달리, 403 응답은 일반적으로 WWW-Authenticate 헤더를 포함하지 않으며,
 왜냐하면 클라이언트에게 다시 인증을 요청하는 것은 무의미하기 때문임.
 서버는 이미 클라이언트가 누구인지 알고 있으며. 문제는 그들의 신원이 아니라 권한임을 알고 있기 때문.
 */