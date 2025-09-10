package com.example.mallapi.todo.dto;

import java.util.List;
import java.util.stream.IntStream;

import lombok.Builder;
import lombok.Data;

@Data
public class PageResponseDTO<E> {  

    private List<Integer> pageNumList;// 페이지 블럭에 해당되는 페이지 시작, 마지막 번호
    private List<E> dtoList;

    private PageRequestDTO pageRequestDTO;
    private int start, end;
    private boolean prev, next;
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(
                                PageRequestDTO pageRequestDTO, // 요청한 페이징 정보
                                List<E> dtoList,        // 요청한 query결과값 
                                long totalCount        // 전체 개수
                                ) {
             
       
        this.pageRequestDTO = pageRequestDTO;// 페이지 정보(현재페이지, 페이지 크기 등)
        this.dtoList = dtoList;  // DTO리스트(실제 페이지 데이터)
        this.totalCount = (int)totalCount; // 전체 데이터 개수

        // 1.  현재페이지 -> 페이지 블럭 계산하기
        
        // 화면에 보여질 페이지 번호에 대한 =>페이지블럭  끝번호 계산
        // 예) 현재 페이지를 10으로 나눈 값을 올림 처리 후 10을 곱하기(7페이지 -> 10, 15페이 -> 20,..)
        this.end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) *10  ;
        this.current = pageRequestDTO.getPage();
        // 화면에 보여질 페이지 번호에 대한 =>페이지블럭  시작 번호 계산
        // 끝 번호에서 9를 뺌 (예: end가 10이면 start는 1, end가 20이면 start는 11)
        this.start = this.end  - 9;

        // 전체 데이터 개수 -> 마지막 페이지 계산
        int last = (int) (Math.ceil((totalCount * 1.0) / pageRequestDTO.getSize()));
        // 페이지 블럭 범위: 마지막 페이지 번호 설정
        this.end = Math.min(this.end, last);

        // 이전, 다음 여부 확인
        this.prev = this.start > 1; //   현재 페이지 블럭범위 시작번호가 1보다 작으면 true 그렇지 않으면 false
        this.next = this.end < last;//  현재 페이지 블럭범위 끝번호가 last보다 작으면 true 그렇지 않으면 false
        this.next = totalCount > this.end*pageRequestDTO.getSize();

        // 해당 페이지 번호에 대한 페이지 블럭 범위 계산
        this.pageNumList = IntStream.rangeClosed(this.start, this.end).boxed().toList();

        // 이전 페이가 존재하면 이전페이지 번호 계산
        if(prev){
            this.prevPage = this.start - 1;
        }
        // 다음 페이가 존재하면 다음페이지 번호 계산
        if (next){
            this.nextPage = this.end+1;
        }

        // 현재 페이지에 보여질 총 데이터 개수
        this.totalPage = this.pageNumList.size();
        // 현재 페이지 번호
        this.current = pageRequestDTO.getPage();

    }
        



}
