package com.example.mallapi.mall.repository.search;

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.QItem;
import com.example.mallapi.mall.domain.QItemImg;
import com.example.mallapi.mall.dto.MainItemDTO;
import com.example.mallapi.mall.dto.QMainItemDTO;
import com.example.mallapi.mall.dto.search.ItemSearchDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 관련 동적 쿼리를 처리하는 QueryDSL 구현체 클래스
 * ItemSearch 인터페이스를 구현하며, 복잡한 검색 조건을 동적으로 생성하여 처리
 */
@Log4j2
@Repository // Spring의 저장소 빈(Bean)으로 등록
public class ItemSearchImpl implements ItemSearch {

    private final JPAQueryFactory queryFactory;

    /**
     * 생성자 주입을 통해 EntityManager를 받아 JPAQueryFactory를 초기화
     * @param em 영속성 컨텍스트를 관리하는 EntityManager
     */
    public ItemSearchImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 판매 상태(판매중, 품절)에 따른 동적 검색 조건을 생성
     * @param searchSellStatus 검색할 판매 상태
     * @return BooleanExpression (QueryDSL의 where 절에서 사용될 조건). searchSellStatus가 null이면 null을 반환하여 이 조건은 무시됨.
     */
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    /**
     * 상품 등록일에 따른 동적 검색 조건을 생성합니다.
     * @param searchDateType 검색 기간 타입 (예: "1d", "1w", "1m", "6m")
     * @return BooleanExpression. searchDateType이 "all"이거나 null이면 null을 반환
     */
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null; // 조건이 없으면 null을 반환하여 where절에서 생략되도록 함
        }
        if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime); // 계산된 시간 이후에 등록된 상품만 조회
    }

    /**
     * 검색어에 따른 동적 검색 조건을 생성합니다. (상품명 또는 등록자 기준)
     * @param searchBy 검색 기준 필드 (예: "itemNm", "createdBy")
     * @param searchQuery 검색어
     * @return BooleanExpression. searchQuery가 비어있으면 null을 반환
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchQuery)) {
            return null; // 검색어가 없으면 조건을 적용하지 않음
        }

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%"); // 상품명으로 검색
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%"); // 등록자로 검색
        }
        return null;
    }

    /**
     * 관리자 페이지에서 사용할 상품 목록을 동적 쿼리와 페이징을 적용하여 조회
     * @param itemSearchDTO 검색 조건 DTO
     * @param pageable 페이징 정보
     * @return 페이징된 상품 목록 (Page<Item>)
     */
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
        log.info("---- getAdminItemPage() ----");

        // 1. 데이터 목록 조회: selectFrom()으로 쿼리 시작
        List<Item> itemList = queryFactory
                .selectFrom(QItem.item) // QItem.item을 사용하여 상품 엔티티를 대상으로 쿼리
                .where( // where 절에 동적 조건들을 콤마(,)로 연결 (AND 조건으로 처리됨)
                        regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                        searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery())
                )
                .orderBy(QItem.item.id.desc()) // 상품 ID 기준 내림차순 정렬
                .offset(pageable.getOffset())   // 페이징 처리: 시작 인덱스
                .limit(pageable.getPageSize())  // 페이징 처리: 페이지당 데이터 수
                .fetch();   // 쿼리 실행 및 결과 리스트 반환

        // 2. 전체 데이터 개수 조회: 페이징을 위해 별도의 count 쿼리 실행
        Long total = queryFactory
                .select(Wildcard.count) // count(*)와 동일
                .from(QItem.item)
                .where( // 데이터 조회와 동일한 조건 적용
                        regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                        searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery())
                )
                .fetchOne(); // 단일 결과(count)를 가져옴

        // 3. PageImpl 객체 생성 및 반환: 조회된 데이터, 페이징 정보, 전체 개수를 담아 반환
        return new PageImpl<>(itemList, pageable, total == null ? 0 : total);
    }

    /**
     * 메인 화면 상품 검색을 위한 상품명 검색 조건을 생성
     * @param searchQuery 상품명 검색어
     * @return BooleanExpression
     */
    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    /**
     * 메인 페이지에 보여줄 상품 목록을 DTO로 직접 조회(프로젝션)하고 페이징하여 반환
     * @param itemSearchDTO 검색 조건 DTO (여기서는 상품명 검색어만 사용)
     * @param pageable 페이징 정보
     * @return 페이징된 상품 DTO 목록 (Page<MainItemDTO>)
     */
    @Override
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {

        // 1. DTO 프로젝션 설정: 엔티티 전체가 아닌, 화면에 필요한 특정 필드만 DTO로 직접 조회
        List<MainItemDTO> content = queryFactory
                .select(
                        // QMainItemDTO는 @QueryProjection으로 생성된 DTO로, 생성자에 필드를 명시
                        new QMainItemDTO(QItem.item.id, QItem.item.itemNm, QItem.item.itemDetail, QItemImg.itemImg.imgUrl, QItem.item.price)
                )
                .from(QItemImg.itemImg) // 상품 이미지(ItemImg)를 기준으로 쿼리 시작
                .join(QItemImg.itemImg.item, QItem.item) // 상품(Item)과 조인
                .where(
                        QItemImg.itemImg.repImgYn.eq("Y"), // 대표 이미지만 필터링
                        itemNmLike(itemSearchDTO.getSearchQuery()) // 상품명으로 검색
                )
                .orderBy(QItem.item.id.desc()) // 최신 상품 순으로 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 데이터 개수 조회 (동일한 조인 및 조건 적용)
        Long total = queryFactory
                .select(Wildcard.count)
                .from(QItemImg.itemImg)
                .join(QItemImg.itemImg.item, QItem.item)
                .where(
                        QItemImg.itemImg.repImgYn.eq("Y"),
                        itemNmLike(itemSearchDTO.getSearchQuery())
                )
                .fetchOne();

        // 3. PageImpl 객체 생성 및 반환
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
