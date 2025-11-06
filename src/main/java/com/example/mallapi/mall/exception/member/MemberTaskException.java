package com.example.mallapi.mall.exception.member;

import lombok.Getter;
import lombok.ToString;

// 특정 오류 상황을 표현하기 위해 RuntimeException을 상속받아 커스텀 예외 클래스 작성
// 회원과 관련된 작업처리 예외가 발생하면 처리하는 커스텀 예외 클래스
@Getter
@ToString
public class MemberTaskException  extends RuntimeException{
    // 예외 처리 객체 속성이름 중 message동일하면 오버라이딩 효과
    //  -> 예외객첵.getMessage()적용시 멤버변수 message값이 추출
    //  -> 주의: message이름을 다른이름으로 사용할 경우 getMessage()값 null, 클라이언트에서 확인됨.
    private final String message; // overriding
    private final int code;

    public MemberTaskException(String msg, int code){
        this.message = msg;
        this.code = code;
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