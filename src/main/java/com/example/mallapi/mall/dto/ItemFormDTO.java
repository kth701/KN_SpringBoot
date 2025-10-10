package com.example.mallapi.mall.dto;

// 상품 데이터 정보를 전달하는 DTO

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter@ToString
@NoArgsConstructor
public class ItemFormDTO {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm; // 상품명

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price; // 가격

    @NotBlank(message = "상품 상세 내용은 필수 입력 값입니다.")
    private String itemDetail; // 상품 상세 설명

    @NotNull(message = "재고 수량은 필수 입력 값입니다.")
    private Integer stockNumber; // 재고수량

    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    private List<ItemImgDTO> itemImgDTOList = new ArrayList<>(); // 상품 이미지 리스트

    // 상품 이미지 아이디(PK)를 저장하는 리스트
    // 상품 등록시에는 아직 상품이미지르 저장하지 않았기 때문에 아무 값도 들어가지 않고
    // 수정시에 이미지 아이디를 담아둘 용도로 사용
    private List<Long> itemImgIds = new ArrayList<>();


    // 1. ModelMapper 적용
    // Item Entity <-> DTO
    private static ModelMapper modelMapper = new ModelMapper();
    public static ItemFormDTO itemFormDtoOf(Item item){
        // Item Entity -> DTO
        return modelMapper.map(item, ItemFormDTO.class);
    }
    // DTO -> Item Entity
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }


    // 2. 직접 작성

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
        itemFormDTO.setItemSellStatus(item.getItemSellStatus());

        return itemFormDTO;
    }
}
