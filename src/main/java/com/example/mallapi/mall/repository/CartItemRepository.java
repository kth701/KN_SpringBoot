package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.CartItem;
import com.example.mallapi.mall.dto.CartDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 1. 현재 로그인한 회원(장바구니 아이디) 장바구니와 상품 아이디를 이용해서 장바구니 상품 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    // 2. 장바구니 페이지에 전달할 CartDetailDTO리스틀 Query문로 조회
    //   : 장바구니 상품, 상품, 상품이미지  Entity
    // 생성자를 통해 DTO반환 new com.example.mallapi.mall.dto.CartDetailDTO(  ) 형식
    // 2.1
    @Query("""
            select new com.example.mallapi.mall.dto.CartDetailDTO(  ci.id, i.itemNm, i.price, ci.count, im.imgUrl )
            from
                CartItem ci, ItemImg im
            join ci.item  i
            where
                ci.cart.id = :cartId and im.item.id = ci.item.id and im.repImgYn = 'Y' 
            order by
                ci.regTime desc
            """)
    List<CartDetailDTO> findCartDetailDtoList(@Param("cartId") Long cartId);

}
