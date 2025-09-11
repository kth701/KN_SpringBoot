package com.example.mallapi.todo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mallapi.todo.dto.PageRequestDTO;
import com.example.mallapi.todo.dto.PageResponseDTO;
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
        log.info("todoDTO={}", todoDTO);

        TodoDTO savedTodoDTO = todoService.register(todoDTO);

        return ResponseEntity.ok(savedTodoDTO);
        //return ResponseEntity.ok().body(savedTodoDTO);
    }

    // 조회 서비스 요청: 잘못된 경로 => 예외처리 발생 => MethodArgumentTypeMismatchException
    @GetMapping("/{tno}") // url = "/api/v1/todos/10"
    public ResponseEntity<TodoDTO> read(@PathVariable("tno")  Long tno){
        log.info("--- RestController read() 호출");
        log.info("tno={}", tno);
        
        TodoDTO todoDTO = todoService.read(tno);

        return ResponseEntity.ok(todoDTO);
    }


    // 수정 서비스 요청
    @PutMapping("/{tno}")  // url = "/api/v1/todos/10"
    public ResponseEntity<TodoDTO> modify(
                @PathVariable("tno") Long tno, 
                @RequestBody TodoDTO todoDTO){

        log.info("--- RestController modify() 호출");
        log.info("tno={}", tno);

        todoDTO.setTno(tno);
        TodoDTO modifiedTodoDTO = todoService.modify(todoDTO);

        return ResponseEntity.ok(modifiedTodoDTO); 
    }
    // 삭제 서비스 요청
    @DeleteMapping("/{tno}")  // url = "/api/v1/todos/10"
    public ResponseEntity<?> remove(@PathVariable("tno") Long tno){
        log.info("--- RestController remove() 호출");
        log.info("tno={}", tno);

        todoService.remove(tno);

        return ResponseEntity.ok(Map.of("result", "success"));
        //return ResponseEntity.ok().body(Map.of("result", "success"));
    }

    // 목록 서비스 요청
    @GetMapping("/list")  // url = "/api/v1/todos/list?page=1&size=10"
    public ResponseEntity<?> list(@Validated PageRequestDTO pageRequestDTO){
        log.info("List Todos: {}", pageRequestDTO);

        PageResponseDTO<TodoDTO> pageResponseDTO 
                                = todoService.getTodoList(pageRequestDTO);

        return ResponseEntity.ok(pageResponseDTO);

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
