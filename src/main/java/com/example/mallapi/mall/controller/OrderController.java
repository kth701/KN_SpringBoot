package com.example.mallapi.mall.controller;


import com.example.mallapi.mall.dto.OrderDTO;
import com.example.mallapi.mall.dto.OrderHistDTO;
import com.example.mallapi.mall.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {
    private final OrderService orderService;


    // 스프링에서 비동기 처리(@ResponseBody, @RequestBody)
    // Principal: 스프링 시큐리티에서 로그인으로 인증시 인등된 정보관리 객체
    // @RequestBody: Http요청의 본문 body에 담긴 내용을 자바 객체로 전달
    // @ResponseBody: 자바 객체를 Http요청의 body로 전달

    // 1. 상품 주문하기
    @PostMapping(value="/order")
    public @ResponseBody ResponseEntity order(
            Principal principal,
            @RequestBody @Valid OrderDTO orderDTO,
            BindingResult bindingResult   ){

        log.info("=> 상품주문 서비스 컨트롤러");

        // 유효성 검사: 주문 정보를 받는 orderDTO객체에 데이터 바인딩 시 에러가 있는지 검사
        if (bindingResult.hasErrors()){
            StringBuffer sb = new StringBuffer();

            // 유효성 검사시 에러가 발생한 필드만 추출
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            // 필드(속성) 에러가 있을 경우 상태코드와 에러 메시지를 클라이언트에 전달
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // principal객체서 현재 로그인한 이메일 정보 얻기: 시큐리티 로그인한 username => 아이디(이메일)
        //String email = principal.getName();
        String email = "user1@test.com"; // 테스트용으로 직접 입력: member테이블에 있는 데이터 사용

        // 주문서가 정상 처리되었을때 생성된 주문서 ID(order_id
        Long orderId;
        try {
            // 상품상세페이지에서 주문한 정보(화면)로 부터 넘어온 주문정보와 회원의 이메일 정보를 이용하여 주문 로직 서비스 호출
            orderId = orderService.order(orderDTO, email);
        } catch (Exception e){
            // 주문 서비스 요청시 예외가 발생했을 경우 : 에러메시지 문자열와 상태코드를 클라이언에 전달
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 결과 값어로 생성된 주문 번호(주문서 id)와 HTTP 응답상태코드 반환
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    // 2. 회원(고객) 구매(주문) 이력 조회
    @GetMapping(value={"/orders", "/orders/{page}"})
    public String orderHist(
            Principal principal,
            @PathVariable("page")Optional<Integer> page,
            Model model
    ){
        // 페이지 설정
        Pageable pageable = PageRequest.of(page.isPresent()?page.get(): 0, 2);

        // 현재 로그인한 회원의 이메일과 페이징 객체를 인자로 전달하여 구매이력 조회
        Page<OrderHistDTO> orderHistDTOSList =
                orderService.getOrderList(principal.getName(), pageable);
        //orderService.getOrderList("test@email.com", pageable); // 테스트


        model.addAttribute("orders", orderHistDTOSList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5); // 페이지 블럭 (한화면에 보여질 페이지 개수)

        log.info("==> 구매이력: OrderController");
        orderHistDTOSList.getContent().forEach( o -> log.info(o));
        return "order/orderHist";
    }

    // 3. 주문 취소 처리
    @PostMapping(value="/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(
            Principal principal,
            @PathVariable("orderId") Long orderId ){

        //String email = "test@email.com";// 테스트

        // 주문 취소시 현재 취소한 사용자(로그인)가 주문자인지 권한 검사
        //if (!orderService.validateOrder(orderId, email)){
        if (!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 주문 취소 서비스 요청
        orderService.cancelOrder(orderId);
        return new ResponseEntity(orderId, HttpStatus.OK);
    }
}
