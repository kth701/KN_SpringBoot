package com.example.mallapi.mall.dto;

import com.example.mallapi.mall.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 주문 상품 정보 => 담은 DTO
// 주문 데이터를 화면에 보낼 때 사용할 DTO클래스
@Setter@Getter@ToString
public class OrderItemDTO {
    private String itemNm;  // 주문 상품 이름
    private int count;      // 주문 수량
    private int orderPrice; // 주문 금액
    private String imgUrl;  // 상품 이미지 경로


    // 생성자:  주문 상품 정보(상품이름, 상품수량, 상품가격), 주문상품 이미지 경로
    public OrderItemDTO(OrderItem orderItem, String imgUrl){
        this.itemNm     = orderItem.getItem().getItemNm();
        this.count      = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl     = imgUrl;
    }


}
