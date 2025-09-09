package com.example.mallapi.todo.service;

import org.springframework.data.domain.Page;

import com.example.mallapi.todo.dto.PageRequestDTO;
import com.example.mallapi.todo.dto.TodoDTO;

public interface TodoService {
    
    // Projections:  Entity -> DTO

    // 등록 기능
    // Long register(TodoDTO todoDTO);
    TodoDTO register(TodoDTO todoDTO);
    // 조회 기능
    TodoDTO read(Long tno);
    // 수정 기능
    TodoDTO modify(TodoDTO todoDTO);
    // 삭제 기능
    void remove(Long tno);
    // 목록 기능
    Page<TodoDTO> list(PageRequestDTO pageRequestDTO);
    

}
