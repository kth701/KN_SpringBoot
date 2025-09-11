package com.example.mallapi.todo.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(1)
    private int page = 1;

    @Builder.Default
    @Min(1)
    @Max(100)
    private int size = 10;

    private String type; // 검색의 종류 title, writer, complete
    private String keyword; // 검색어

    public Pageable getPageable(Sort sort) {
        int pageNum = page < 0 ? 1 : page - 1;
        int sizeNum = size <=10 ? 10: size;

        return PageRequest.of(page - 1, size, sort);
    }


}
