package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import com.example.mallapi.mall.dto.CartItemDTO;
import com.example.mallapi.mall.repository.CartItemRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import com.example.mallapi.mall.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Log4j2
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private EntityManager entityManager;

    private Member createTestMember() {
        Member member = Member.builder()
                .email("testuser@example.com")
                .pw("1111")
                .nickname("TestUser")
                .build();
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    private Item createTestItem() {
        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .itemDetail("상품 상세 설명")
                .stockNumber(100)
                .build();
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("장바구니 담기 테스트")
    void addCart() {
        // given (준비)
        Member member = createTestMember();
        Item item = createTestItem();
        int count = 2; // 담을 수량
        CartItemDTO cartItemDTO = new CartItemDTO(item.getId(), count);

        // when (실행)
        Long cartItemId = cartService.addCart(cartItemDTO, member.getEmail());

        // then (검증)
        entityManager.flush(); // DB 변경사항 강제 반영
        entityManager.clear(); // 영속성 컨텍스트 초기화

        assertNotNull(cartItemId, "장바구니 상품 ID는 null이 아니어야 합니다.");

        com.example.mallapi.mall.domain.CartItem savedCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품이 DB에 저장되지 않았습니다."));

        assertEquals(item.getId(), savedCartItem.getItem().getId(), "요청한 상품과 저장된 상품이 동일해야 합니다.");
        assertEquals(count, savedCartItem.getCount(), "요청한 수량과 저장된 수량이 동일해야 합니다.");
    }

    @Test
    @DisplayName("장바구니 중복 상품 담기 테스트")
    void addCart_existingItem_shouldIncreaseCount() {
        // given (준비)
        Member member = createTestMember();
        Item item = createTestItem();
        int initialCount = 1;
        int additionalCount = 2;

        // 먼저 상품 1개를 장바구니에 담는다.
        CartItemDTO initialDto = new CartItemDTO(item.getId(), initialCount);
        Long initialCartItemId = cartService.addCart(initialDto, member.getEmail());

        // when (실행): 같은 상품을 2개 더 담는다.
        CartItemDTO additionalDto = new CartItemDTO(item.getId(), additionalCount);
        Long updatedCartItemId = cartService.addCart(additionalDto, member.getEmail());

        // then (검증)
        entityManager.flush();
        entityManager.clear();

        assertEquals(initialCartItemId, updatedCartItemId, "새로운 항목이 아닌 기존 항목이 업데이트되어야 합니다.");

        com.example.mallapi.mall.domain.CartItem savedCartItem = cartItemRepository.findById(updatedCartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        int expectedCount = initialCount + additionalCount;
        assertEquals(expectedCount, savedCartItem.getCount(), "기존 수량에 새로운 수량이 더해져야 합니다.");
    }
}
