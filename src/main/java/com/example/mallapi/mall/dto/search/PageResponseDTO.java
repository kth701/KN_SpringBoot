package com.example.mallapi.mall.dto.search;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/* 서버페이지(thymeleaf) -> 페이징 처리위한 PageResponseDTO */

@Getter@ToString
@Log4j2
public class PageResponseDTO<E> {

    // 클라이언트로 부터 요청받은 현재 페이지 번호
    private int page;
    // 클라이언트로 부터 요청받은 1페이지에 읽어올 자료(data) 개수
    private int size;
    // 자료 총 개수
    private int total;

    // 페이지블럭(한페이지 보여질 페이지 범위):
    // 시작페이지 번호, 끝 페이지 번호
    private int start;
    private int end;

    private boolean prev; // 이전 페이지 유무 판별
    private boolean next; // 다음 페이지 유무 판별

    // 한 페이지에 보여질 data를 보관하는 List객체
    // 자료형 타입은 객체호출시 설정
    private List<E> dtoList;

    // 생성자:페이징 초기화
    @Builder(builderMethodName = "withAll") // 메서드
    public PageResponseDTO( PageRequestDTO pageRequestDTO, List<E> dtoList, int total){

        // 자료가 없으면 메서드 실행 종료
        if (total < 0) return;

        this.page = pageRequestDTO.getPage(); // 요청한 페이지 번호
        this.size = pageRequestDTO.getSize(); // 페이지 단위의 자료 개수
        this.total = total;
        this.dtoList = dtoList;

        // 페이지 블럭에서 페이지 범위 계산 처리
        //  현재페이지번호/10.0 => 자리올림 => *10 : 현재페이지의 해당되는  페이지블럭의 끝번호 생성
        // 1~10사이 => 1 => 1*10 => 10,  11~20 => 2 => 2*10 => 20,....
        this.end = (int)(Math.ceil(this.page/10.0)) * 10;
        // 현재페이지 번호에 해당되는 페이지 블럭의 시작 번호 생성
        this.start = this.end - 9;

        // 총페이지수 = 총레코드(자료)rotn / 1페이지 보여질 data개수 => 소수점 이하는 자리올림
        // 1024/10 => 102.4 => 103 총페이지수 : Math.ceil()-> 자리올림
        int last = (int) (Math.ceil(total/(double) size));

        // 마지막 페이지 번호 설정: 페이지블럭의 끝번호가 총페이지수 보다 크면 총페이지수가 마지막 페이지 번호로 설정
        this.end = end > last ? last : end;

        // 페이지 블럭의 시작페이지가 1보다 크면 prev에 true설정
        this.prev = this.start > 1;// true
        // 블럭의 끝 페이지 번호의 총 레코드수가 전체 총레코드 총 개수 보다 크면 false, 그렇지 않으면 true
        // 총 12페이지 인경우: 12page = 120,  페이지블럭의 끝번호: 20, 20*10 => 200
        this.next = total > this.end * this.size;


        // -------------------------------------- //
        log.info("현재페이지: "+this.page);
        log.info("해당 블럭 시작 번호:"+this.start);
        log.info("해당 블럭 끝 번호:"+this.end);

        log.info("이전 버튼 사용 여부:"+this.prev);
        log.info("다음 버튼 사용 여부"+this.next);


    }


}
