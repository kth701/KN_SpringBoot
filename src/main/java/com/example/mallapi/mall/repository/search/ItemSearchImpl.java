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

@Log4j2
@Repository // Spring의 저장소 빈(Bean)으로 등록
public class ItemSearchImpl implements  ItemSearch{

    // 1. 동적 쿼리 생성하기(JPAQueryFactory)
    private final JPAQueryFactory queryFactory;
    // 1.1 생성자로 EntityManager개체 넣어줌
    public ItemSearchImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ---------------------------------------------------------- //
    // 2. 상품 관련 동적 쿼리
    //    : 조건식 처리 하기(SQL의 where 구절을 생성)
    // ---------------------------------------------------------- //

    // 2.1 판매유형 조건 처리식 객체 생성:  판매중 or  품절 상태라면 해당 조건의 상품만 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        // "this(item.itemSellStatus) == searchSellStatus" 형식으로 반환
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // 2.2 판매 기간(시간) 조건식 객체 생성
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now(); // 현재 시간 초기화

        // thymeleaf의 StringUtils
        if (StringUtils.equals("all", searchDateType )|| searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d", searchDateType )){
            // dateTime의 시간을 1일 전으로 세팅 한 후 최근 1일 동안 등록된 상품만 조회하도록 조건값을 반환
            dateTime = dateTime.minusDays(1);
        }else if (StringUtils.equals("1w", searchDateType )){
            // dateTime의 시간을 1주일 전으로 세팅
            dateTime = dateTime.minusWeeks(1);
        }else if (StringUtils.equals("1m", searchDateType )){
            // dateTime의 시간을 1개월 전으로 세팅
            dateTime = dateTime.minusMinutes(1);
        }else if (StringUtils.equals("6m", searchDateType )){
            // dateTime의 시간을 6개월 전으로 세팅
            dateTime = dateTime.minusMonths(6);
        }

        // 상품 등록 시간이 dateTime세팅 시간 이후로 조건식 처리
        return QItem.item.regTime.after(dateTime); // "this(item.regTime) > dateTime"
    }

    // 2.3 검색 키워드 객체 생성
    // searchBy값에 따라 상품명에 검색어를 포함고 있는 상품 또는 상품 생성자이 아이디에 검색어를 포함하고 있는 상품을 조건하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("itemNm", searchBy)){// 상품 이름 기준=> searchBy가 itemNm(상품명)이면 처리
            // this(item.itemNm) like "%키워드%" => ex) item.itemNm  like "%사과%" 형식으로 설정됨.
            return QItem.item.itemNm.like("%"+searchQuery+"%"); // 상품명에 검색어가 포함되어 있으면
        }else if (StringUtils.equals("createdBy", searchBy)) {// 등록자 이름 기준
            // this(item.createdBy) like "%키워드%" => ex) item.createdBy  like "%홍길동%" 형식으로 설정됨.
            return QItem.item.createdBy.like("%"+searchQuery+"%");// 작성자에 로그인 사용자 이름이 포함되어 있으면
        }
        return null;
    }

    // 2.4 상품 List 조회
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
        // 상품조회 조건 정보:itemSearchDTO, 페이징 정보:pageable
        log.info("---- getAdminItemPage()");

        // 1. 조건에 부합하는 자료 조회
        //   : queryFactory객체를 이용해서 쿼리를 생성
        List<Item> itemList =
                queryFactory
                        .selectFrom(QItem.item)             // entity(테이블명)
                        .where( // where 구절 : 콤마단위(",")넣을 경우 and조건으로 처리됨
                                //  // 상품 등록 시간이 dateTime세팅 시간 이후로 조건식 처리 => "item.regTime > dateTime"
                                regDtsAfter(itemSearchDTO.getSearchDateType()),  // (콤마: ',') and로 인식된
                                // 판매 상태(판매중, 품점)
                                searchSellStatusEq(itemSearchDTO.getSearchSellStatus()), //
                                // 조건 필드명(상품명, 등록자), 검색 키워드
                                // item.itemNm  like "%사과%"  or item.createdBy  like "%홍길동%"
                                searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery())

                                //특징 => ","(콤마) 단위일 경우 and 조건으로 구성됨.
                        )
                        .orderBy(QItem.item.id.desc())
                        //  Pageable에서 첫번째페이지 번호 : 0부터 설정, 1페이지를 0페이지 맵핑 형태
                        .offset(pageable.getOffset())   // 데이터를 가져올 시작 인덱스(index)
                        .limit(pageable.getPageSize())  // 페이지에 가져올 최대 개수
                        .fetch();   // 조회 대상 리스트 반환(collection구조)

        // fetchResults(), fetch(), fetchOne(), fetchFirst(), fetchCount()

        log.info("contents: {}",itemList);

        // 2. 조건에 부합하는 자료 개수: select count(*) from where 조건식

        Long total = queryFactory
                .select(Wildcard.count)
                .from(QItem.item)
                .where(
                        regDtsAfter(itemSearchDTO.getSearchDateType()),
                        searchSellStatusEq(itemSearchDTO.getSearchSellStatus()),searchByLike(itemSearchDTO.getSearchBy(), itemSearchDTO.getSearchQuery()))
                .fetchOne()    ;

        log.info("total: {}",total);

        // PageImpl => Page객체 생성
        return new PageImpl<>(itemList, pageable, total == null ? 0 : total);
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

        Long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDTO.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
