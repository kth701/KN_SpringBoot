package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Cart;
import com.example.mallapi.mall.domain.CartItem;
import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.CartDetailDTO;
import com.example.mallapi.mall.dto.CartItemDTO;
import com.example.mallapi.mall.dto.CartOrderDTO;
import com.example.mallapi.mall.dto.OrderDTO;
import com.example.mallapi.mall.repository.CartItemRepository;
import com.example.mallapi.mall.repository.CartRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import com.example.mallapi.mall.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {
    // 장바구니 상품 : 장바구니정보(회원정보), 상품정보
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니 상품을 주문하기 위한 서비스 요청
    private final OrderService orderService;

    // -------------------------------------------------------------------- //
    // 1. 상품을  장바구니에 담기(상품id, 상품수량, 장바구니 소유자:회원
    // -------------------------------------------------------------------- //
    public Long addCart(CartItemDTO cartItemDTO, String email){

        // 1.1 장바바구니 상품아이디로  상품정보 조회
        Item item = itemRepository.findById(cartItemDTO.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 1.2 현재 로그인한 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 1.3 현재로그인 회원(email)로 장바구니 찾아 오기
        Cart cart = cartRepository.findByMemberEmail(member.getEmail());

        // 1.4 회원의 장바구니 없을 경우 장바구니 생성:  특정 회원이 장바구니 호출을 한번도 사용하지 않은 경우
        if (cart == null){
            cart = Cart.createCart(member); // 현재 로그인한 회원의 정보를 가지고 장바구니 생성
            cartRepository.save(cart);      // DB반영
        }

        // 1.5 장바구니 상품 : 장바구니에 등록된 상품 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (savedCartItem != null){
            // 장바구니에 있는 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼 증가
            savedCartItem.addCount(cartItemDTO.getCount());
            return savedCartItem.getId();
        } else {
            // 장바구니에 없는 상품일 경우
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDTO.getCount());
            cartItemRepository.save(cartItem);

            return cartItem.getId();
        }

    }

    // -------------------------------------------------------------------- //
    // 2. 로그인한 회원의 정보를 이용하여 장바구니에 담긴 장바구니 상품 조회
    // -------------------------------------------------------------------- //
    @Transactional(readOnly = true)
    public List<CartDetailDTO> getCartList(String email){

        // email => Security 로그인 성공시 principal.getName()값인 username
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();

        // 2.1 로그인한 회원의 정보
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberEmail(member.getEmail());// 장바구니와 회원아이디 맵핑관계

        // 2.2 장바구니 엔티티가 없으면 비어 있는 List반환
        if (cart == null){
            return cartDetailDTOList = cartItemRepository.findCartDetailDtoList(cart.getId());
        }

        // 2.3 장바구니에 담은 상품 조회
        cartDetailDTOList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDTOList;
    }


    // -------------------------------------------------------- //
    // 3. 장바구니 수정 권한 체크는 메서드 선언
    // -------------------------------------------------------- //
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        // 현재 로그인 회원 조회
        Member currMember = memberRepository.findByEmail(email);
        // 장바구니 상품 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        // 장바구니 상품 Entity를 통행 Cart Entity에 맵핑된 Member Entity 호출
        // 장바구니 회원 정보 추출
        Member savedMember = cartItem.getCart().getMember();

        // 로그인한 회원과 장바구니 회원이 동일한지 판별
        if (!StringUtils.equals(currMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }
    // -------------------------------------------------------------------- //
    // 4. 장바구니 상품 수량 업데이트
    // -------------------------------------------------------------------- //
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    // -------------------------------------------------------------------- //
    // 5. 장바구니 상품 삭제
    // -------------------------------------------------------------------- //
    public void deleteCartItem(Long cartItemId){

        // 5.1 장바구니에서 삭제할 상품추출
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        // 5.2 장바구니 상품 삭제
        cartItemRepository.delete(cartItem);

    }


    // -------------------------------------------------------------------- //
    // 6. 장바구니 상품 주문하기: CartItem -> OrderItem
    // -------------------------------------------------------------------- //
    public Long orderCartItem(List<CartOrderDTO> cartOrderDTOList, String email){

        // 6.1 OrderDTO: 주문할 상품아이디, 수량 정보 담아 놓는 DTO
        List<OrderDTO> orderDTOList = new ArrayList<>();

        for (CartOrderDTO cartOrderDTO : cartOrderDTOList){
            //log.info("------");
            //log.info("=> CartOrderDTO Service:"+cartOrderDTO);

            // 6.27 장바구니 주문상품아이디 => 주문상품(CartItem) Enity 정보 호출(주문상품아이디, 수량)
            CartItem cartItem = cartItemRepository.findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            // 6.3 장바구니 상품 List => 주문 상품 List 전환
            // 주문할 상품정보를 보관하는 OrderDTO에 장바구니 상품(CartItem)정보 가져오기
            OrderDTO orderDTO = new OrderDTO();

            // 장바구니 상품에 연결된 상품아이디를 가지고 주문상품(OrderItem) Entity생성에 사용
            orderDTO.setItemId(cartItem.getItem().getId());
            orderDTO.setCount(cartItem.getCount());

            orderDTOList.add(orderDTO);
        }

        // 6.4 주문 상품 서비스 요청: Order, OrderItem Entity생성 -> DB반영
        Long orderId = orderService.orders(orderDTOList, email);

        // 6.5 주문이 완료되면 장바구니에 있는 상품 List 삭제
        for (CartOrderDTO cartOrderDTO : cartOrderDTOList){

            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            // 6.6 장바구니 상품 삭제 수행
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}

