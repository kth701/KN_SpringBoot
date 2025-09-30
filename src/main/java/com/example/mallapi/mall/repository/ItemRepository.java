package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // ----------------------------------------------------------------------------- //
    // 1.1 단순 Query문 처리시:  find + (엔티티이름) + By + 변수(필드)명
    // ----------------------------------------------------------------------------- //
    // 상품 목록
    List<Item> findByItemNm(String itemNm);
    // 상품명과 상품 상셍 설명을 OR 조건 조회
    // 상품 가격이 전달된 매개변수보다 값이 작은 상품 조회
    // 정렬
    // Spring DATA JPA @query어노테이션  => 파리미터 이름 => '%:매개변수이름%'
    // 기존 DB에 사용하던 쿼리문 그대로 사용시 : nativeQuery=true 설정


}
