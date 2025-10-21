package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    // 현재 로그인한 회원 Cart조회 => 회윈 이메일 기준
    Cart findByMemberEmail(Long memberEmail);
}
