package com.example.mallapi.mall.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="order_item")
@Getter
@Setter
public class OrderItem {
    @Id // 기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)// auto_incrument 적용
    @Column(name="order_item_id")
    private Long id;// 주문 상품id

    // 하나의 상품은 여러 주문 상품으로 들어 갈 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;// 주문 상품과 상품 관계

    // 한 번의 주문에 여러 개의 상품을 주문 할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;// 주문서와 주문 상품관계

    private int orderPrice;// 주문 가격
    private int count;// 수량

    private LocalDateTime regTime;
    private LocalDateTime updateTime;

}
/*
1. 연관관계의 주인은 외래키가 있는 곳으로 설정
2. 연관 관계의 주인이 외래키를 관리(등록, 수정, 삭제)

3. 주인이 아닌 쪽은 연관 관계 매핑시 mappedBy속성의 값으로 연관 관계의 주인을 설정
4. 주인이 아닌 쪽은 읽기만 가능

 */


/* 주문서에 주문할 주문상품 관계 맵핑
 하나의 주문서에는 여러 주문 상품을 담을 수 있는 관계

Orders      <--- 1:N --->      order_item
order_id(PK)                   order_item_id(PK), order_id(FK)
...                            ...
 */