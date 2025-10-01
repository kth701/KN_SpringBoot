package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
