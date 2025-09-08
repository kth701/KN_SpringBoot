package com.example.mallapi.todo.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.entity.TodoEntity;

public interface TodoSearch {

    // 검색 관련 메서드 : Querydsl
    Page<TodoEntity> search1(Pageable pageable);
    Page<TodoDTO> searchDTO(Pageable pageable);
    

}
