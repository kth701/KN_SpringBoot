package com.example.mallapi.mall.dto;

import com.example.mallapi.constant.OrderStatus;

import com.example.mallapi.mall.domain.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 주문 정보 => 담은 DTO
@Setter
@Getter
@ToString
public class OrderHistDTO {
    private Long orderId;                     // 주문서 ID(아이디)
    private String orderDate;               // 주문 날짜 => 날짜 형식 전환
    private OrderStatus orderStatus;    // 주문 상태(주문:ORDER, 취소:CANCEL)
    // 주문 상품 리스트
    private List<OrderItemDTO> orderItemDTOList =new ArrayList<>();

    // 1. 생성자 : Order Entity 정보 읽기 -> DTO에 저장
    public OrderHistDTO(Order order){
        this.orderId = order.getId();
        // 날짜형 타입 -> 문자열 타입
        this.orderDate =
                order.getOrderDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    // 2. 주문 상품 정보(OrderItemDTO) 읽어 와서 주문상품 정보List에 저장
    public void addOrderItemDTO(OrderItemDTO orderItemDTO){
        orderItemDTOList.add(orderItemDTO);
    }

}