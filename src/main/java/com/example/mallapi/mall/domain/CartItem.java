package com.example.mallapi.mall.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    // 같은 상품을 장바구니에 몇 개 담을지 저장
    private int count;



    // ------------------------------------------------------------------------------------- //
    // 1. 장바구니에 담을 상품 Entity
    // ------------------------------------------------------------------------------------- //
    public static CartItem createCartItem(Cart cart, Item item, int count){
        // 1.1 장바구니 상품 Entity생성: 누구의 장바구니인지 식별, 장바구에 담을 상품 식별, 수량
        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);

        return cartItem;
    }

    // ------------------------------------------------------------------------------------- //
    // 2. 장바구니에 담을 상품 수량 증감처리
    //     : 해당 상품 추가로 장바구니에 담을 때 기존 수량에 현재 담을 수량을 더해 주는 메서드
    // ------------------------------------------------------------------------------------- //
    public void addCount(int count){
        this.count+=count; // 기존 수량 증가
    }

    // ------------------------------------------------------------------------------------- //
    // 3. 장바구니 수량변화에 따른 업데이트처리(상세페이지 View에서 수량변화에 따른 업데이트)
    // ------------------------------------------------------------------------------------- //
    public void updateCount(int count) { this.count = count;}

}
