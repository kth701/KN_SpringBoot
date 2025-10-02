package com.example.mallapi.mall.dto;

// 상품 데이터 정보를 전달하는 DTO

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemFormDTO {
    private Long id;
    private String itemNm; // 상품명
    private Integer price; // 가격
    private String itemDetail; // 상품 상세 설명
    private Integer stockNumber; // 재고수량
    private String itemSellStatus; // 상품 판매 상태

    private List<ItemImgDTO> itemImgDTOList = new ArrayList<>(); // 상품 이미지 리스트

    // 상품 이미지 아이디(PK)를 저장하는 리스트
    // 상품 등록시에는 아직 상품이미지르 저장하지 않았기 때문에 아무 값도 들어가지 않고
    // 수정시에 이미지 아이디를 담아둘 용도로 사용
    private List<Long> itemImgIds = new ArrayList<>();



    // item dto -> entity
    public Item toEntity() {
        return Item.builder()
                .id(id)
                .itemNm(itemNm)
                .price(price)
                .itemDetail(itemDetail)
                .stockNumber(stockNumber)
                .itemSellStatus(
                        itemSellStatus.equals("SELL") ? ItemSellStatus.SELL : ItemSellStatus.SOLD_OUT)
                .build();
    }

    // item entity -> DTO
    public static ItemFormDTO of(Item item) {
        ItemFormDTO itemFormDTO = new ItemFormDTO();

        itemFormDTO.setId(item.getId());
        itemFormDTO.setItemNm(item.getItemNm());
        itemFormDTO.setPrice(item.getPrice());
        itemFormDTO.setItemDetail(item.getItemDetail());
        itemFormDTO.setStockNumber(item.getStockNumber());
        itemFormDTO.setItemSellStatus(item.getItemSellStatus().toString());

        return itemFormDTO;
    }
}
