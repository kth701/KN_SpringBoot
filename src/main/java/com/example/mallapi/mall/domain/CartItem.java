package com.example.mallapi.mall.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart_item")
@Getter@Setter
public class CartItem {
    @Id // 기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)// auto_incrument 적용
    @Column(name="cart_item_id")
    private Long id; // 장바구니 상품 식별(어느장바구니 상품인지, 어느 상품인지)

    // 하나의 장바구니 -> 여러개의 상품을 담을 수 있다
    @ManyToOne(fetch = FetchType.LAZY) // 게으론 로딩(필요할 때 연결)
    @JoinColumn(name="cart_id")
    private Cart cart;
    // 하나의 상품 -> 여러 장바구니에 장바구니 상품을 담을 수 있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int count;


}
