package com.example.mallapi.mall.service;

import com.example.mallapi.mall.repository.ItemRepository;
import com.example.mallapi.mall.repository.MemberRepository;
import com.example.mallapi.mall.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Log4j2
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("주문 테스트")
    void order() {
    }

    @Test
    void getOrderList() {
    }

    @Test
    void validateOrder() {
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void orders() {
    }
}