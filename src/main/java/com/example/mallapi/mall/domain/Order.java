package com.example.mallapi.mall.domain;

import com.example.mallapi.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order extends BaseEntity {
    @Id // 기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)// auto_incrument 적용
    @Column(name="order_id")
    private Long id;// 주문서 식별id

    // 한명의 회원 -> 여러 주문을 할 수 있다.
    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩-> 필요시 연결(proxy객체보관)
    @JoinColumn(name="email")
    private Member member;// 주문 회원(참조하는 Entity)

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태(주문, 취소)


    // 하나의 주문서에는 여러 주문 상품을 담을 수 있는 관계 설정
    // 주문 상품 List객체 생성
    // 외래키가 아닌 엔티티를 주인으로 설정시 :
    //  mappedBy속성=>List객체가 주체가 아니고 List안에 있는 orders가 주체임을 명시
    @OneToMany(mappedBy = "order",// OrderItem에 있는 Order에 의해 관리된다는 의미(Order[외래키인 주인]와 OrderItem은 1:N관계)
            cascade = CascadeType.ALL, // 부모 엔티티의 영속성 상태 변화 -> 주문엔티티 저장하면서 주문상품 엔티티 함께 저장
            orphanRemoval = true, // 고아객체 제거(부모 엔티티 삭제 시, 자식 엔티티 같이 제거)
            fetch = FetchType.LAZY) // 필요시 연결(속도 개선)
    private List<OrderItem> orderItems = new ArrayList<>();

//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

}




/* 한명의 회원은 여러번 주문할 수 있는 관계 맵핑
Member      <-- 1:N -->     Order
member_id(PK)               order_id(PK)
name, email,...             member_id(FK):외래키-> 주체
                            order_date,...
 */

/*
1. 연관관계의 주인은 외래키가 있는 곳으로 설정
2. 연관 관계의 주인이 외래키를 관리(등록, 수정, 삭제)

3. 주인이 아닌 쪽은 연관 관계 매핑시 mappedBy속성의 값으로 연관 관계의 주인을 설정
4. 주인이 아닌 쪽은 읽기만 가능
 */