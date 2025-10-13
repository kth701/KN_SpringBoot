package com.example.mallapi.mall.repository.search;

import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.dto.MainItemDTO;
import com.example.mallapi.mall.dto.search.ItemSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemSearch {

    // 1. 상품 List 조회
    // 페이징 처리 인터페이스(내용, 페이징)는 Page<T>타입으로 반환
    Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable);

    // 2. 메인화면에 표시될 상품 List 조회
    // @QueryProjection 설정: Entity -> DTO 객체 바로 생성
    Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable);


}
