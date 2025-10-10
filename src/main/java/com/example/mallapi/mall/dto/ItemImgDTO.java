package com.example.mallapi.mall.dto;

// 상품 이미지에 대한 데이터를 전달하는 DTO

import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.ItemImg;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter@Setter
public class ItemImgDTO {
    private Long id; // 상품 이미지 식별ID(PK)
    private String imgName; // 상품 이미지 파일명
    private String oriImgName; // 상품 원본 이미지 파일명
    private String imgUrl; // 이미지 조회 경로

    private String repimgYn; // 대표 이미지 여부

    // ------------------------------------------------ //
    // DTO -> Entity, Entity -> DTO 전환하는 메서드 구현
    // ------------------------------------------------ //

    // 1. ModelMapper 이용
    private static ModelMapper modelMapper = new ModelMapper();
    public static ItemImgDTO of(ItemImg itemImg){
        return modelMapper.map(itemImg, ItemImgDTO.class);// entity -> DTO 전환
    }
    // DTO -> Entity
    public Item createItemImg(){
        return modelMapper.map(this, Item.class);
    }



    // 2. 직접 작성:테스트 필요
    // DTO 생성자에 파라미터로 전달받은 Entity -> DTO 전환
    public ItemImgDTO(ItemImg itemImg){
        this.id = itemImg.getId();
        this.imgName = itemImg.getImgName();
        this.oriImgName = itemImg.getOriImgName();
        this.imgUrl = itemImg.getImgUrl();
        this.repimgYn = itemImg.getRepImgYn();
    }
    // DTO -> Entity 전환:
    public ItemImg toEntity(){
        return ItemImg.builder()
                .id(id)
                .imgName(imgName)
                .oriImgName(oriImgName)
                .imgUrl(imgUrl)
                .repImgYn(repimgYn)
                .build();
    }


}
