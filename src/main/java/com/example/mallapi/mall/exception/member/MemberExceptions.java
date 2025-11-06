package com.example.mallapi.mall.exception.member;

// 회원과 관련된 작업처리 예외가 발생시 처리하는 예외메시지 처리및 커스텀 예외발생 클래스 객체 구현
public enum MemberExceptions {

    // 1. 예외처리 메시지 정의

    /*   */
    NOT_FOUND("NOT_FOUND", 404),
    DUPLICATE("DUPLICATE", 409),
    INVALID("INVALID",400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401);

    /*
    NOT_FOUND("회원을 찾을 수 없습니다.", 404),
    DUPLICATE("중복된 회원 정보입니다.", 409),
    INVALID("유효하지 않은 요청입니다.", 400),
    BAD_CREDENTIALS("인증 정보가 올바르지 않습니다.(아이디 또는 비밀번호 불일치)", 401);
    */
    private final MemberTaskException memberTaskException;

    // 1. 생성자로 통해 MemberTaskException클래스 객체 생성하기
    MemberExceptions(String msg, int code){
        this.memberTaskException = new MemberTaskException(msg, code);
    }
    // 2.  memberTaskException객체 추출(내보내기)
    public MemberTaskException get(){
        return memberTaskException;
    }

}

/*
400 code : 클라이언트가 요청을 수정하여 다시 보내야 문제가 해결됨을 의미
            사용자 등록 요청을 보내면서 필수 항목인 username 필드를 누락했을 때.
            나이(age) 필드에 숫자가 아닌 문자열을 포함했을 때.
            요청 본문(Body)을 유효하지 않은 JSON 형식으로 보냈을 때.
401 code: Unauthorized
 */