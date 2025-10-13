package com.example.mallapi.mall.exception;

// 상품 주문수량보다 재고의 수가 적을 때 발생시킬 exception 정의
public class OutOfStockException extends  RuntimeException{
    public OutOfStockException(String message){
        super(message);
    }
}