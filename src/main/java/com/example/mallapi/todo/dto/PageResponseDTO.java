package com.example.mallapi.todo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class PageResponseDTO<E> {  

    private List<Integer> pageNuIntegers;// 페이지 블럭에 해당되는 페이지 시작, 마지막 번호
    private List<E> dtoList;

    private PageRequestDTO pageRequestDTO;
    private int start, end;
    private boolean prev, next;
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(
                                PageRequestDTO pageRequestDTO, // 요청한 페이징 정보
                                List<E> dtoList,    // 요청한 query결과값 
                                int totalCount  // 전체 개수
                                ) {
                                    
        this.pageRequestDTO = pageRequestDTO;
        this.dtoList = dtoList;
        this.totalCount = (int)totalCount;

        this.end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) ;
        this.current = pageRequestDTO.getPage();
        this.start = this.end * 10 - 9;
    }
        



}
