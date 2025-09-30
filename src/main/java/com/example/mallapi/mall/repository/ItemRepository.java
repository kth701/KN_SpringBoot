package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;


import java.util.List;

/*
JpaRepository 인터페이스
    <S extends T> S save(S entity) : 엔티티 저장 및 수정
    void delete(T enttity): 엔티티 삭제
    count(): 엔티티 총 개수 반환
    iterable<T> findAll(): 모든 엔티티 조회
 */

//public interface ItemRepository extends JpaRepository<Item, Long> {
public interface ItemRepository extends JpaRepository<Item, Long>,
                                        QuerydslPredicateExecutor {
    // Predicate: '조건에 맞다'고 판단하는 근거를 함수로 제공
    // -> Repository에 Predicate를 파라미터로 전달하기 위해서 QuerydslPredicateExecutor인터페이스를 상속
    // 결론 -> QuerydslPredicateExecutor인터페이스를 상속 받으면 조건처리할 수 있는 함수를 제공

    // ----------------------------------------------------------------------------- //
    // 1.1 단순 Query문 처리시:  find + (엔티티이름) + By + 변수(필드)명
    // ----------------------------------------------------------------------------- //

    // 상품 목록
    List<Item> findByItemNm(String itemNm);
    // 상품명과 상품 상세 설명을 OR 조건 조회
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // 상품 가격이 전달된 매개변수보다 값이 작은 상품 조회
    List<Item> findByPriceLessThan(Integer price);
    // 정렬
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    // Spring DATA JPA @query어노테이션  => 파리미터 이름 => '%:매개변수이름%'

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    // 기존 DB에 사용하던 쿼리문 그대로 사용시 : nativeQuery=true 설정
    // 특징: MariaDB인경우는  like사용시 concat()함수 사용
    @Query(value = "select * from item where item_detail like concat('%', :itemDetail, '%') order by price desc",
            nativeQuery = true)
    List<Item> findByItemDetailNative(@Param("itemDetail") String itemDetail);




}
