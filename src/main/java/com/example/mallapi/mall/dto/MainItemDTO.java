package com.example.mallapi.mall.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 메인 페이지에 표시할 상품 정보
@Getter@Setter@ToString
public class MainItemDTO {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    // @QueryProjection
    // Entity값은 받은 후  DTO클래스 변환하는 과정 없이 바로 DTO객체로 전환
    // Gradle -> Tasks -> other -> compilJava : DTO생성

    // 생성자
    @QueryProjection
    public MainItemDTO( Long id, String itemNm, String itemDetail, String imgUrl, Integer price){
        this.id = id;
        this.itemNm = itemNm;
        this.imgUrl = imgUrl;
        this.itemDetail = itemDetail;
        this.price = price;
    }
}
