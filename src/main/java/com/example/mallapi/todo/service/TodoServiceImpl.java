package com.example.mallapi.todo.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.entity.TodoEntity;
import com.example.mallapi.todo.repository.TodoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class TodoServiceImpl implements TodoService {

    // ModelMapper 객체 주입
    private final ModelMapper modelMapper;
    private final TodoRepository todoRepository;
    

    @Override
    // public Long register(TodoDTO todoDTO) {
     public TodoDTO register(TodoDTO todoDTO) {

        log.info("---- Todo Service 구현 중");

        // 클라이언트로 부터 전달 받은 자료 -> DTO 로 전달 받아 저장

        // Entity ->  : modelMapper.map(객체 값, 엔티티.class)
        // 1.  ModelMapper이용
        // TodoEntity todoEntity = modelMapper.map(todoDTO, TodoEntity.class );  

        // 2.  TodoDTO 메서드 이용
        TodoEntity todoEntity = todoDTO.toEntity();


         // Entity -> DB에 반영
        TodoEntity saveTodoEntity = todoRepository.save(todoEntity);

        // return saveTodoEntity.getTno();
        return new TodoDTO(todoEntity);

    }

}
