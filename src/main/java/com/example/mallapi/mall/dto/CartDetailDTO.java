package com.example.mallapi.mall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class CartDetailDTO {

    private Long cartItemId;        // 장바구니 상품 아이디
    private String itemNm;          // 상품명
    private int price;              // 상품 금액
    private int count;              // 상품 수량
    private String imgUrl;          // 대표 상품 이미지



    // 장바구니 페이지에 전달할 데이터를 생성자의 파라미터로 전달
    public CartDetailDTO(
            Long cartItemId,
            String itemNm,
            int price,
            int count,
            String imgUrl  ){

        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }


}
