package com.example.mallapi.mall.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 상품 상세페이지 -> 주문할 상품아이디, 주문 수량을 전달 받아 유효성 검사
@Getter@Setter@ToString
public class OrderDTO {
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value=1,   message ="취소 주문 수량은 1개 입니다.")
    @Max(value=999, message = "최대 주문 수량은 999개 입니다.")
    private int count;
}
