package com.example.mallapi.mall.service;


import com.example.mallapi.mall.domain.*;
import com.example.mallapi.mall.dto.OrderDTO;
import com.example.mallapi.mall.dto.OrderHistDTO;
import com.example.mallapi.mall.dto.OrderItemDTO;
import com.example.mallapi.mall.repository.ItemImgRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import com.example.mallapi.mall.repository.MemberRepository;
import com.example.mallapi.mall.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderService {
    private final ItemRepository itemRepository;            // 상품 정보중 가격, 재고수량
    private final MemberRepository memberRepository;        // 주문 고객
    private final OrderRepository orderRepository;          // 주문 정보

    private final ItemImgRepository itemImgRepository;      // 주문상품목에 적용될 상품이미지 정보

    // 1. 상품 주문 서비스(상품 아이디, 수량, 로그인 고객:주문 고객)
    public Long order(OrderDTO orderDTO, String email){

        // 1.1 주문한 상품 아이디 -> 상품 정보 추출(상품 상세페이지에서 주문할 상품 엔티티와 주문 수량 정보 이용) -> OrderDTO
        Item item = itemRepository.findById( orderDTO.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 1.2 현재 로그인 한 회원의 이메일(아이디)를 이용해서 회원 정보 조회
        Member member = memberRepository.findByEmail(email);
        // 1.3 주문 상품 목록 저장할 List구조 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        // 1.4 주문상품 DTO(상품 상세페이지에서 주문할 상품 엔티티와 주문 수량:OrderDTO)로 통해 주문 상품 Entity객체 생성하기
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount()); // 주문 상품정보, 수량
        //1. 5. 주문상품 Entity객체를 List에 저장하기
        orderItemList.add(orderItem);

        // 1.6 주문서 Entity생성 : 주문한 회원정보, 주문한 상품 List
        Order order = Order.createOrder(member, orderItemList);
        // 1.7 생성한 주문 엔티티를 저장
        orderRepository.save(order);
        // 1.8 주문서 id 반환
        return order.getId();

    }


    // 2. 주문 이력(주문 상품 목록) 서비스
    @Transactional(readOnly = true)
    public Page<OrderHistDTO> getOrderList(String email, Pageable pageable){
        // 1. 사용자 아이디와 페이징 조건을 이용해 주문 목록 요청
        List<Order> orders  = orderRepository.findOrder(email, pageable);
        // 2. 총 주문 개수
        Long totalCount = orderRepository.countOrder(email);

        // 3. 검색하여 가져온 주문 목록을 순회하여 구매이력 페이지에 전달할 List객체 생성
        List<OrderHistDTO> orderHistDTOS = new ArrayList<>();
        for (Order order : orders){

            OrderHistDTO orderHistDTO = new OrderHistDTO(order);


            /*
            1. JPA N+1문제
            @OneToMany 등에서 하위엔티티들을 lazy loading으로 자져올 때마다 자식 조회 쿼리가 추가발생
            - 성능 저하의 요인 : for문 순회할 때마다 매번 조회쿼리문이 추가 되어 실행
            ex) ~ from orderItem where orderId = ? 매번 반복 수행하는 현상

            2. 개선 방안
               properties파일설정 => "spring.jpa.properties.hibernate.default_batch_fetch_size=숫자"

               default_batch_fetch_size=> 하위 엔티티를 로딩할 때 한번에 상위 엔티티 id를 지정한 숫자만큼 inQuery로딩
               개선 =>  ~ from orderItem where orderId in (?,?,?,?,...)  */

            //-----------------------------------------------------------  //
            // 주문서에 있는 주문상품 Entity -> DTO에 전달 : N+1 문제 발행
            //-----------------------------------------------------------  //
            List<OrderItem> orderItems = order.getOrderItems();
            //----------------------------------------------------------  //

            for (OrderItem orderItem: orderItems){
                // 1. 대표 상품이미지: 주문상품 이고 대표 상품이미지인 조건 검색(and조건)
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");

                // 2. 주문한 상품 정보와 대표 상품이지 url
                //    : 주문상품 이력에 표시할 주문상품 정보 DTO에 저장
                OrderItemDTO orderItemDTO = new OrderItemDTO(orderItem, itemImg.getImgUrl());

                // 3. 주문 상품 이력 List
                orderHistDTO.addOrderItemDTO(orderItemDTO);

                log.info("--- 주문 상품 이력: OrderService ---");
                log.info(orderItem.getItem().toString());
                log.info(orderItemDTO.toString());

            } // end for

            // 주문이력 List객체에 주문이력 상품 추가: 주문1, 주문2,....
            orderHistDTOS.add(orderHistDTO);
        }

        // 페이지 구현 객체 생성0
        return new PageImpl<>(orderHistDTOS, pageable, totalCount);
    };

    // 3. 주문 취소시 : 로그인 한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        //  현재 로그인 회원의 이메일 정보
//        Member currMember = memberRepository.findByEmail(email);

        // 주문서에 등록된 회원 정보(이메일) : 실제 주문한 회원 이메일
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        Member savedMember = order.getMember();
        // 현재 로그인한 회원 이메일과 주문서에 등록된 회원이메일 동일한지 검사
//        if (!StringUtils.equals(currMember.getEmail(), savedMember.getEmail()))
//            // 일치하지 않으면 (현재 로그인 회원과 주문서에 있는 회원 이메일 동일하지 않으면 ) false반환
//            return false;

        return true; // 동일한 회원이면 true 반환
    }

    // 4. 주문 취소시 : transaction 변경 감지 기능에 의해서 트랜잭션이 끌날 때 upate쿼리 실행
    //   : 주문 취소 기능 수행 => order entity에 주문 상태를 취소로 변경, OrderItem 통해 Item Entity 재고수량 변경
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        order.cancelOrder();
    }


    // --------------------------------------------------------------- //
    // 5. 장바구에 담겨 있는 주문상품 주문하기 서비스
    //    : 주문상품(OrderItem) Entity, 주문(Order) Entity 생성, DB반영
    // --------------------------------------------------------------- //
    // OrderDTO: 주문할 상품아이디, 수량 정보 담아 놓는 DTO
    public Long orders(List<OrderDTO> orderDTOList, String email){


        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDTO orderDTO : orderDTOList){
            //log.info("------");
            log.info("=> CartOrderDTO OrderService:"+orderDTO);

            // 5.1 orderDTO : 주문상품아이디, 수량
            Item item = itemRepository.findById(orderDTO.getItemId()).orElseThrow(EntityNotFoundException::new);

            // 5.2 orderDTO 값을 통해 OrderItem Entity 객체 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount());
            // 5.3 생성된 orderItem Entity를 List구조 객체에 저장
            orderItemList.add(orderItem);
        }

        // 5.3 주문상품(OrderItem) Entity와 주문(Order) Entity 연관 맵핑
        Order order = Order.createOrder(member, orderItemList);
        // 5.4 생성된 Order Entity DB반영
        orderRepository.save(order);

        return order.getId();
    }


}
