package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Cart;
import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import com.example.mallapi.mall.dto.MemberDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Test처리한 다음 rollback처리 (원상복귀)
@Log4j2
//@TestPropertySource(locations = "classpath:application-test.yml")
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder itemRepository;

    @PersistenceContext
    EntityManager em;

    // 회원 등록
    public Member createMember(){
        // member dto 생성 -> entity -> save() -> saved 엔티티 반환
        MemberDTO memberDTO = new MemberDTO(
                "test2@test.com",// username
                "1111",// password
                "test2",// nickname
                false,// social
                false,//del
                List.of("USER")
        );

        // dto -> entity
        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .pw(memberDTO.getPw())
                .nickname(memberDTO.getNickname())
                .social(memberDTO.isSocial())
                .del(memberDTO.isDel())
                .build();
        // dto내용중 list구조만 따로 처리
        for(String roleName: memberDTO.getRoleNames()){
            member.addRole(MemberRole.valueOf(roleName));
        }

        return member;
    }

    @Test
    @DisplayName("장바구니, 회원 에티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // test용 => @Transactional어노테이션 있을 경우 적용
        em.flush();// 강제로 DB반영
        em.clear();// 영속성 컨텍스트에 엔티티 비우기

        Cart savedCart =
                cartRepository
                        .findById(cart.getId())
                        .orElseThrow(EntityNotFoundException::new);

        log.info("-> savedCart:{}", savedCart.toString());

    }



}