package com.example.mallapi.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.service.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/todos")
public class APIController {

    private final TodoService todoService;


    // 등록작업 처리하기 전에 먼저 검증(validation) => 서버 쪽에서 전달된 데이터를 검증 필요
    // 데이터 검증 실패 => MethodArgumentNotValidException 예외 처리 발생
    @PostMapping("") // 클라이언트로 부터 전달 받은 데이터 타입 => JSON,..
    public ResponseEntity<TodoDTO> register(@RequestBody @Validated TodoDTO todoDTO){
        log.info("--- RestController register() 호출");
        log.info("todoDTOL {}", todoDTO);

        TodoDTO savedTodoDTO = todoService.register(todoDTO);

        return ResponseEntity.ok(savedTodoDTO);
        //return ResponseEntity.ok().body(savedTodoDTO);
    }





}

/* 컨트롤러 계층 설계
 * 
 * 등록 => /api/v1/todos (POST) => 등록 서비스와 매칭
 * 조회 => /api/v1/todos/번호(GET) => 조회 서비스와 매칭
 * 수정 => /api/v1/todos/번호(PUT) =>  수정 서비스와 매칭
 * 삭제 => /api/v1/todos/번호(DELETE) => 삭제 서비스와 매칭
 * 목록 => /api/v1/todos/list(GET) => 목록 서비스와 매칭
 * 
 */
