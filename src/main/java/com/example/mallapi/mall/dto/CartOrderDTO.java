package com.example.mallapi.mall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

// 장바구니 페이이지에서 주문할 상품 데이터를 전달할 DTO
@Getter@Setter@ToString
public class CartOrderDTO {
    private Long cartItemId;

    // CartOrderDTO자기 자신을 List로 가지고 있도록 구조 설정
    private List<CartOrderDTO> cartOrderDTOList;
}
