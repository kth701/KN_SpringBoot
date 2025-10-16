package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. 현재 로그인 사용자의 주문 데이터를 페이지 조건에 맞춰서 조회
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc, o.orderStatus")
    List<Order> findOrder(@Param("email") String email, Pageable pageable);

    // 2. 현재 로그인 한 회원의 주문 개수가 몇 개인지 조회
    @Query("select count(o) from Order o where o.member.email = :email ")
    Long countOrder(@Param("email") String email);

    // 3. 현재 로그인 사용자의 주문 데이터를 페이지 조회시 검색 조건 추가시
    //    동적쿼리문구현 => itemSearch, itemSearchImpl 참조
}
