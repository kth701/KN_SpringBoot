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
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
public class ItemSearchImpl implements  ItemSearch{

    // 1. 동적 쿼리 생성하기
    private JPAQueryFactory queryFactory;
    public ItemSearchImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ---------------------------------------------------------- //
    // 2. 상품 관련 동적 쿼리
    //   조건식 처리 하기(SQL의 where 구절을 생성)
    // ---------------------------------------------------------- //

    // 2.1 판매유형
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        // "this(item.itemSellStatu) == searchSellStatus" 형식으로 반환
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // 2.2 판매 기간
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        // thymeleaf의 StringUtils
        if (StringUtils.equals("all", searchDateType )|| searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d", searchDateType )){
            dateTime = dateTime.minusDays(1);
        }else if (StringUtils.equals("1w", searchDateType )){
            dateTime = dateTime.minusWeeks(1);
        }else if (StringUtils.equals("1m", searchDateType )){
            dateTime = dateTime.minusMinutes(1);
        }else if (StringUtils.equals("6m", searchDateType )){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime); // "this(item.regTime) > dateTime"
    }

    // 2.3 검색 키워드
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("itemNm", searchBy)){// 상품 이름 기준
            // this(item.itemNm) like "%키워드%"
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }else if (StringUtils.equals("createdBy", searchBy)) {// 등록자 이름 기준
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }

    // 2.4 상품 List 조회
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
        log.info("---- getAdminItemPage()");

        // 1. 조건에 부합하는 자료 조회
        List<Item> itemList =
                queryFactory
                        .selectFrom(QItem.item)             // entity(테이블명)
                        .where( // where 구절
                                regDtsAfter(itemSearchDTO.getSearchDateType()),
                                // 판매 상태(판매중, 품점)
                                searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                                // 조건 필드명(상품명, 등록자), 검색 키워드
                                searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery())
                                // ","(콤마) 단위일 경우 and 조건으로 구성됨.
                        )
                        .orderBy(QItem.item.id.desc())
                        .offset(pageable.getOffset())   // 데이터를 가져올 시작 인덱스
                        .limit(pageable.getPageSize())  // 1페이지에 가져올 최대 개수
                        .fetch();   // 조회 대상 리스트 반환(collection구조)

        // fetchResults(), fetch(), fetchOne(), fetchFirst(), fetchCount()

        log.info("contents: "+itemList);

        // 2. 조건에 부합하는 자료 개수: select count(*) from where 조건식

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),
                        searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery()))
                .fetchOne()
                ;



        log.info("total: "+total);

        // PageImpl => Page객체 생성
        return new PageImpl<>(itemList, pageable, total);
    }



    // ---------------------------------------------------------- //
    // 메인 화면에 보여줄 상품 조회(List)
    // ---------------------------------------------------------- //
    // 3. 메인화면에 표시될 상품 List 조회
    // @QueryProjection 설정: Entity -> DTO 객체 바로 생성

    // 3.1 메인 화면 검색 키워드 (조건식)
    private BooleanExpression itemNmLike(String searchQuery){
        //  ~ from item  where itemNm like '%키워드%'
        return StringUtils.isEmpty((searchQuery))? null: QItem.item.itemNm.like("%"+searchQuery+"%");
    }

    // 3.2 메인 화면 동적 query문
    @Override
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {

        // 도메인에 있는 상품 Entity, 상품 이미지 Entity 도메인 객체 생성
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // Entity -> DTO 객체 바로 생성
        List<MainItemDTO> content = queryFactory
                .select(
                        new QMainItemDTO( item.id, item.itemNm, item.itemDetail, itemImg.imgUrl, item.price)
                )
                .from(itemImg)
                // inner join( 교집합) : 상품엔티티와 상품이미지 entity가 일치하는 상품만 추출
                .join(itemImg.item, item)
                //  대표이미지만 추출
                .where(itemImg.repImgYn.eq("Y"))
                // 특정 상품 검색
                .where(itemNmLike(itemSearchDTO.getSearchQuery()))
                // id(auto_increment)기준 내림차순(최근에 등록된 상품)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                // 한페이지에 설정된 데이터 개수만큼 추출
                .limit(pageable.getPageSize())
                .fetch();

        long totoal = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDTO.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, totoal);
    }
}
