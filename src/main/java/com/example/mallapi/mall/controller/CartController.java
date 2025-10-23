package com.example.mallapi.mall.controller;

import com.example.mallapi.mall.dto.CartDetailDTO;
import com.example.mallapi.mall.dto.CartItemDTO;
import com.example.mallapi.mall.dto.CartOrderDTO;
import com.example.mallapi.mall.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final CartService cartService;

    // 1. 상세페이지에서 장바구니 담기 기능 요청시 처리
    @PostMapping(value="/cart")
    public @ResponseBody ResponseEntity order(
            Principal principal,
            @RequestBody @Valid CartItemDTO cartItemDTO,
            BindingResult bindingResult){

        // 1.1장바구니에 담을 상품정보(상품아이디, 수량) 데이터 검증
        if (bindingResult.hasErrors()){
            // 1.1.1 에러 메시지 처리할 객체
            StringBuilder sb = new StringBuilder();
            // 1.1.2 에러가 발생한 필드만 추출하여 List에 저장
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            // 1.1.3 필드항목 순회하여 에러 항목별로 에러 메시지 작성
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);

        }

        // 1.2 현재 로그인한 회원의 이메일(아이디)정보를 추출
        String email = "user1@test.com";
//        String email = principal.getName();
        Long cartItemId;

        try {
            // Cart, cartItem 객체 생성(장바구니, 장바구니상품)
            cartItemId = cartService.addCart(cartItemDTO, email);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 1.3 장바구니 기능 정상 수행후 :  장바구니ID와 상태코드 응답
        return new ResponseEntity(cartItemId, HttpStatus.OK);

    }

    // 2. 장바구니 페이지 이동
    @GetMapping(value="/cart")
    public String orderHist(
            Principal principal, // Security 로그인 회원 정보 추출: username(email)
            Model model){

        String email = "user1@test.com";// test용 => Security 로그인 적용시 주석 처리
        //String email = principal.getName();

        // 현재 로그인한 회원의 장바구니 상품 조회
        List<CartDetailDTO> cartDetailDTOList = cartService.getCartList(email);
        //List<CartDetailDTO > cartDetailDTOList = cartService.getCartList(principal.getName());

        // 장바구니 상품 조회 결과 객체에 담아 View페이지 이동
        model.addAttribute("cartItems", cartDetailDTOList);
        return "mall/cart/cartList";
    }

    // ------------------------------------------------------------------------ //
    // REST API 방식 : PATCH (요청 자원의 일부만 처리)
    // ------------------------------------------------------------------------ //

    // 3. 장바구니 상품 수정
    @PatchMapping(value="/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(
            Principal principal,
            @PathVariable("cartItemId" ) Long cartItemId,
            int count
    ){
        log.info("==> 주문상품:"+cartItemId+","+count);

        //String email = principal.getName();
        String email = "user1@test.com"; // test

        if (count <= 0 ){
            // 장바구니 상품 개수가 0이하면 에러메시지와 상태코드 반환
            return new ResponseEntity("최소 1개이상 담아 주세요",HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, email)){
            // 로그인한 회원의 장바구니인지 판별
            return new ResponseEntity<String>("수정 권한이 없습니다. ", HttpStatus.FORBIDDEN);
        }

        // 장바구니 상품 수량 업데이트 요청
        cartService.updateCartItemCount(cartItemId, count);

        // 장바구니에 있는 상품 수량 업데이트 처리 요청
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }


    // 4. 장바구니 상품 삭제
    @DeleteMapping(value="/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(
            Principal principal,
            @PathVariable("cartItemId") Long cartItemId
    ){

        // 4.1 로그인한 회원의 장바구니인지 판별
        //String email = principal.getName();
        String email = "user1@test.com"; // test
        if(!cartService.validateCartItem(cartItemId, email)){
            return new ResponseEntity<String>("삭제 권한이 없습니다. ", HttpStatus.FORBIDDEN);
        }


        // 4.2 장바구니에 있는 상품 삭제 서비스 요청
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    // 5 장바구니 상품 주문하기
    @PostMapping(value="/cart/orders") // consumes = "application/json"
    public @ResponseBody ResponseEntity orderCartItem(
            Principal principal,
            @RequestBody CartOrderDTO cartOrderDTO
    ){

        // JSON객체 => CartOrderDTO List객체 맵핑
        List<CartOrderDTO> cartOrderDTOList = cartOrderDTO.getCartOrderDTOList();
        log.info("=> cartOrders");
        log.info(cartOrderDTOList);

        if (cartOrderDTOList == null || cartOrderDTOList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        String email = "user1@test.com"; // Security 로그인 미적용시 테스트용
        for (CartOrderDTO cartOrder : cartOrderDTOList ){
            // Security 로그인 미적용 : 테스트용 로그인 상태
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), email)){
            // Security 로그인 시 적용
            //if (!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }// end for

        // 장바구니 상품 주문 서비스 요청하기
        // Security 로그인 미적용 : 테스트용 로그인 상태
        Long orderId = cartService.orderCartItem(cartOrderDTOList, email);
        // Security 로그인시 적용
        //Long orderId = cartService.orderCartItem(cartOrderDTOList, principal.getName());


        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
