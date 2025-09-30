package com.example.mallapi.mall.repository;

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ItemRepositoryTest {

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


}
