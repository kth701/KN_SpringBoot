package com.example.mallapi.mall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

// 장바구니 페이이지에서 여러개의 상품을 주문하므로 CasrtOrderDTO클래스가 자기 자신을 List로 저장
@Getter@Setter@ToString
public class CartOrderDTO {
    private Long cartItemId;

    // CartOrderDTO자기 자신을 List로 가지고 있도록 구조 설정
    private List<CartOrderDTO> cartOrderDTOList;
}
