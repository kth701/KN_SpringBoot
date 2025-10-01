package com.example.mallapi.mall.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter@Setter
@ToString
public class Cart {
    @Id // 기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)// auto_incrument 적용
    @Column(name="cart_id")
    private Long id;  // 장바구니 식별id

    // 현재 entity가 다른 entity참조하는 구조이면 반드시 외래키 및 맵핑관계 설정
    @OneToOne(fetch =  FetchType.EAGER) // 즉시 로딩:fetch속성 생략하면  FetchType.EAGER설정됨
    @JoinColumn(name="email")   // 참조하는 필드명 설정(Member엔티티의 email필드 PK)
    private Member member;      // 특정 엔티티를 참조

    // 회원 장바구니 생성 메서드
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}





/*
연관 관계 맵핑 종류
일대일(1:1) -> @OneToOne -> 회원 엔티티와 장바구니 엔티티 맵핑
일대다(1:N) -> @OneToMany -> 하나의 장바구니와 여러개의 장바구니상품
다대일(N:1) -> @ManyToOne -> 여러개의 상품과 하나의 장바구니
다대다(N:M)

// 테이블에서 관계는 항상 양방향
// 객체 관계는 단반향, 양방향

Cart        <-- 1:1 -->     Member
cart_id(PK)                 member_id(PK)
member_id(FK): 외래키(참조)   name, email, password, ...

외래키(FK) -> 특정 테이블의 필드가 다른 테이블의 필드값을 참조하는데 그 필가가 기본키(PK)인 경우
 */