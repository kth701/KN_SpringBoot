package com.example.mallapi.mall.repository;

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ItemRepositoryTest {
    // Test에서 Spring DATA JPA Querydsl적용
    @PersistenceContext
    EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Transactional
    @Commit
    @DisplayName("상품 등록 테스트")
    public void testInsert() {
        IntStream.rangeClosed(1,10).forEach(i -> {
            Item item = Item.builder()
                    .itemNm("테스트 상품"+i)
                    .price(1000+i)
                    .itemDetail("테스트 상품 상세 설명"+i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .stockNumber(100)
                    .regTime(LocalDateTime.now())       // 속성 상속시 builder()적용 안됨.
                    .updateTime(LocalDateTime.now()) // 속성 상속시 builder()적용 안됨.
                    .build();

            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            if( i%3==0 ){
                item.setRegTime(LocalDateTime.now().minusDays(1));
            } else if (i%5==0){
                item.setRegTime(LocalDateTime.now().minusWeeks(1));
            } else if (i%7==0){
                item.setRegTime(LocalDateTime.now().minusMonths(1));
                item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            } else if (i%10==0){
                item.setRegTime(LocalDateTime.now().minusMonths(6));
            }

            Item savedItem = itemRepository.save(item);

            /*
            Item Entity -> toString()적용시 내용이 표시되지 않을 경우
            @ToString 어노테이션에 callSuper = true 속성을 추가하여 부모 클래스의 toString() 메서드도 함께 호출
             */
            log.info("=> 저장된값:"+savedItem.toString());
            log.info("=>날짜1:"+LocalDateTime.now().toString());
            log.info("=>날짜2:"+LocalDateTime.now().minusDays(1).toString());
            log.info("=>item:"+item.getRegTime());
            log.info("=>saveditem:"+savedItem.getRegTime());

            Assertions.assertNotNull(savedItem.getId());
        });

    }


    // 상품조회
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        // 상품 등록
        //this.createItemTest();

        // 상품 조회
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");

        itemList.stream().forEach( item -> {
            log.info("=>"+item);
        });

    }

    @Test
    @DisplayName("상품명, 상품상세설명 OR 테스트")
    public void findByItemNmOrItemDetail(){
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        itemList.stream().forEach( item -> {
            log.info("-> {}",item);
        });
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThan(){
        List<Item> itemList = itemRepository.findByPriceLessThan(1005);
        itemList.stream().forEach( item -> log.info("-> Price LessThan: {}",item));
    }
    @Test
    @DisplayName("가격 내림차순: LessThan OrderBy Desc 태스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(1005);
        itemList.stream().forEach( item -> log.info("-> Price LessThan OrderBy Desc: {}",item));
    }

    // ------------------------ //
    // Spring Data JPA Querydsl //
    // ------------------------ //
    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void querydslTest() {
        //this.createItemTest();
        //List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        List<Item> itemList = itemRepository.findByItemDetailNative("테스트 상품 상세 설명");
        itemList.stream().forEach(item -> log.info("-> Price LessThan OrderBy Desc: {}", item));
    }


    /*
    JPA 동작 방식
    Entiry Manager Factory -> create -> Entity Manager Instance -> operation -> Persistence Context(Entity,...)

    Entity Manger Factory :  Entity Manager Instance를 관리하는 주체
        -> 애플리케이션 실생시 한 개만 생성되며, 요청이 오면 엔티티 매니저 팩토리로부터 엔티티메니저를 생성
    엔티티 매니저: 영속성 컨텍스트에 접근하여 엔티티에 대한 데이터베이스 작업을 제공(CRUD)
        -> 내부적으로 커넥션에 사용해서 데이터베이스 접근
    영속성 컨텍스트(Persistence Context): 엔티티를 영구 저장하는 환경으로 엔티티 매니저를 통해 영속성 컨텍스트에 접근
    엔티티: 데이터베이스의 테이블에 대응하는 클래스-> JPA에서 관리
     */

    @Test
    @DisplayName("JPA Querydsl 조회 테스트2")
    public void querydslTest2() {
        // EntityManager -> JPAQueryFactory에 위임 -> 데이터베이스에 접근에서 작업처리
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem) // select * from item_id
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL)) // where item_sell_status = 'SELL'
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%")) // and item_detail like '%테스트 상품 상세 설명%'
                .orderBy(qItem.price.desc());// order by price desc

        // 1. List<T> fetch() : 조회 결과를 List타입으로 반환
        List<Item> itemList = query.fetch();
        itemList.stream().forEach(item -> log.info("-> JPA Querydsl 조회 테스트2: {}", item));

        // 2. Long fetchCount(): 총 개수 -> select count(*) from item
        long total = query.fetchCount();
        log.info("-> fetchCount():{}",total);

        // 3. T fetchFirst(): 조회대상 중 첫번째 1건만 반환
        Item item = query.fetchFirst();
        log.info("-> fetchFirst():{}",item);

        // 4. T fetchOne(): 조회 결과가 하나일 경우
        JPAQuery<Item> query2 = queryFactory
                .selectFrom(qItem)
                .where(qItem.id.eq(5L)); // select * from item where id = 5

        item = query2.fetchOne();
        log.info("-> fetchOne():{}",item);

    }

    @Test
    @DisplayName("BooleanBuilder객체 -> Querydsl 조회 테스트3")
    public void querydslTest3() {
        List<Item> allList = itemRepository.findAll();
        allList.stream().forEach(allItem -> log.info("-> allList: {}", allItem));
        log.info("------------");

        // BooleanBuilder: 조건식을 처리하는 객체
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // 쿼리문 대상 Q도메인 -> Item Entity지칭
        QItem qItem = QItem.item;

        // 조건처리할 값 설정
        String itemDetail = "테스트 상품 상세 설명";
        int price = 1005;
        String search_itemSellStatus = "SELL";

        // 판매상태 조건 판단 여부 => 조건식 추가(생성)
        if(StringUtils.equals( search_itemSellStatus, ItemSellStatus.SELL ))
            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL)); // where item_sell_status = 'SELL'

        // 페이지 정보에 대한 설정 ( 페이지번호, 해당 페이지에 가져올 데이터 개수)
        Pageable pageable = PageRequest.of(0, 2);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);

        List<Item> itemList = itemPagingResult.getContent();
        log.info("--- BooleanBuilder적용한 페이징 관련 메서드 ");
        log.info("-> 전체 데이터 개수: {}",itemPagingResult.getTotalElements());
        log.info("-> 전체 페이지 수: {}",itemPagingResult.getTotalPages());
        log.info("-> 현재 페이지 번호: {}",itemPagingResult.getNumber());
        log.info("-> 해당 페이지 데이터 수: {}",itemPagingResult.getSize());
        log.info("-> 첫번째 페이지인지 여부: {}",itemPagingResult.isFirst());
        log.info("-> 마지막 페이지인지 여부: {}",itemPagingResult.isLast());
        log.info("-> 다음 페이지가 있는지 여부: {}",itemPagingResult.hasNext());
        log.info("-> 이전 페이지가 있는지 여부 : {}",itemPagingResult.hasPrevious());















    }







}
