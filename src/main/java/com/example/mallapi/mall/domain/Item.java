package com.example.mallapi.mall.domain;

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString//@ToString(callSuper = true)
@NoArgsConstructor@AllArgsConstructor
@Builder
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO) // 시퀸스
    private Long id;            // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;    // 상품이름

    @Column(nullable = false, name="price")
    private int price;          // 상품가격

    @Column(nullable = false)
    private int stockNumber;    // 상품재고

    @Lob
    @Column(nullable = false)
    private String itemDetail;  // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    /*
    private LocalDateTime regTime;//등록 시간
    private LocalDateTime updateTime;// 수정시간
    private String createdBy;// 등록자
    private String modifiedBy;// 수정자
    */


    // ---------------------------------------------------------- //
    // 상품 정보, 재고 수량 수정 수행하는 메서드 정의
    // ---------------------------------------------------------- //


    // 1. 수정 작업 처리: 엔티티 필드(속성) 수정하는 메서드
    public void change(String itemNm, String itemDetail){
        this.itemNm = itemNm; this.itemDetail = itemDetail;
    }

    // 2. 수정 폼으로부터 변경된 상품정보(DTO)를 entity에 전달
    public void updateItem(ItemFormDTO itemFormDTO){
        this.itemNm = itemFormDTO.getItemNm();
        this.price = itemFormDTO.getPrice();
        this.stockNumber = itemFormDTO.getStockNumber();
        this.itemDetail = itemFormDTO.getItemDetail();
        this.itemSellStatus = itemFormDTO.getItemSellStatus();
    }

    // 3. 재고 수량 수정하기

    // 3.1 주문시 재고수량 업데이트
    public void removeStock(int stockNumber){
        int resStock = this.stockNumber - stockNumber;

        if (resStock < 0){

            // 3.1.1 재수량이 부족할 경우 사용자가 작성한 예외 처리 발생
            throw new OutOfStockException("상품 재고가 부족합니다. (현재 재고수량: "+this.stockNumber+")");
        }

        // 3.1.2 재고 수량이 부족하지 않을 경우
        this.stockNumber = resStock;
    }

    // 3.2 주문 취소시 주문 수량 만큼 재고수량에 증가 시키는 메서드
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
