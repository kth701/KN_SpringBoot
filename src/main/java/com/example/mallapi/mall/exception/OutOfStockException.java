package com.example.mallapi.mall.exception;

// 상품 주문수량보다 재고의 수가 적을 때 발생시킬 exception 정의
public class OutOfStockException extends  RuntimeException{
    public OutOfStockException(String message){
        super(message);
    }
}

/*
RuntimeException
주로 논리적인 오류나 잘못된 사용 때문에 발생. 예를 들어, NullPointerException (널 값에 접근 시도), ArrayIndexOutOfBoundsException (배열의 유효 범위를 벗어난 인덱스 접근), IllegalArgumentException (메서드에 부적절한 인자 전달) 등.
    코드 간결성:
        try-catch 강제를 피하면서도 의미 있는 예외를 던질 수 있어 코드가 불필요하게 복잡해지는 것을 막고 개발 편의성을 높여줌
    사용자 정의 예외:
        개발자가 애플리케이션의 특정 오류 상황을 표현하기 위해 RuntimeException을 상속받아 커스텀 예외 클래스를 만드는 경우
 */