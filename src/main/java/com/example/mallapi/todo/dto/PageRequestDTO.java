package com.example.mallapi.todo.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    private String type; // 검색의 종류 title, writer, complete
    private String keyword; // 검색어

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }


}
