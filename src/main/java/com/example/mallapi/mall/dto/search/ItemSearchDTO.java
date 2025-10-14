package com.example.mallapi.mall.dto.search;

import com.example.mallapi.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class ItemSearchDTO {
    // 1. 상품 등록일 비교해서 상품 데이터 조회
    /* 기간 조회
        all : 상품 등로일 전체
        1d: 최근 하루 동안 등록된 상품
        1w: 최근 일주일
        1m: 최근 한달
        6m: 최근 6개월
     */
    private String searchDateType;


    //2. 상품 판매 상태를 기준으로 조회
    private ItemSellStatus searchSellStatus;

    // 3. 상품 유형를 기준으로 조회
    //   - 유형: itemNm(상품명), createdBy(상품 등록자 아이디)
    private String searchBy;

    // 4. 상품 유형 기준으로 검색어 기준으로 조회
    //   - 상품명 기준 검색, 상품 등록자 아이디 기준 검색
    private String searchQuery = "";
}
