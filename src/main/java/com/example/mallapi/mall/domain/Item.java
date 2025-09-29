package com.example.mallapi.mall.domain;

import java.time.LocalDate;

import com.example.mallapi.constant.ItemSellStatus;
import groovy.transform.ToString;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Item")
@Getter@Setter
@ToString
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO) // 시퀸스
    private Long id;            // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemName;    // 상품이름

    @Column(nullable = false, name="price")
    private int price;          // 상품가격

    @Column(nullable = false)
    private int stockNumber;    // 상품재고

    @Lob
    @Column(nullable = false)
    private String itemDetail;  // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    private LocalDate regTime;  // 등록시간
    private LocalDate updateTime;// 수정시간




}
