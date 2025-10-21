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
    private OrderStatus orderStatus; // 주문 상태(주문:ORDER, 취소:CANCEL)


    // 하나의 주문서에는 여러 주문 상품을 담을 수 있는 관계 설정:
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



    // --------------------------------------------------------------------- //
    // 기능 수행 (메서드 작성) : 주문에 관련 정보 설정(주문할 상품, 수량 등 )
    // --------------------------------------------------------------------- //

    // 1. 주문 상품 정보 담기: 주문상품, 주문서
    public void addOrderItem(OrderItem orderItem){
        // 1.1. 주문 상품을 주문 상폼 목록(List구조)에 저장
        orderItems.add(orderItem);

        // Order엔티티와 OrderItem 엔티티 양방향 참조 관계 : orderItem객체에도 order객체를 세팅(설정)
        // 1.2 현재 주문서(Order)정보를 주문상품(OrderItem)에 주문(Order)정보 등록
        //     => OrderItem에는 주문서와 주문상품 연관관계설정 되어 있음
        orderItem.setOrder(this);
    }

    // 2. 주문 내역 구성 : 주문 상품 목록, 주문 고객(회원: 로그인회원), 주문 상태
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        // 2.1 주문내역을 처리하는 객체 생성
        Order order = new Order();

        // 2.2. 현재 로그인 한 고객 정보
        order.setMember(member);
        // 2.3 주문 상품목록 저장
        for (OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        // 2.4 주문상태 정보(주문, 취소)
        order.setOrderStatus(OrderStatus.ORDER);
        // 2.5 주문 날짜
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    // 3. 주문한 상품 총 금액 계산 처리
    public int getTotalPrice(){
        int totalPrice = 0;

        // 주문상품 목록에서 금액을 모두 합산
        for(OrderItem orderItem: orderItems){
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

    // 4. 주문 상품 취소시 : 상품재고 재구성
    public void cancelOrder(){
        // 주문상태 변경: 주문 상태 -> 주문 취소 상태로 전환
        this.orderStatus = OrderStatus.CANCEL;

        // 주문상품 목록에 있는 상품수량을 가지고 상품재고 수정
        for (OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }





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