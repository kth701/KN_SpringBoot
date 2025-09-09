package com.example.mallapi.todo.dto;

import java.time.LocalDate;

import com.example.mallapi.todo.entity.TodoEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodoDTO {
    private Long tno;
    private String title;
    private String writer;
    private boolean complete;

    // "2025-9-4"과  같은 포맷으로 구성
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate; // 날짜를 문자열로 처리...


    // --------------------- //
    // Projectons와 DTO   //
    // --------------------- //
    // 엔티티 객체를 반환하는 대신에 DTO객체를 반환하는 기능
    // @Query 또는 Querydsl을 이용하는 경우 사용
    
    // TodoDTO생서자로 TodoEntity를 파라미터로 전달받아  Entity -> DTO 변환
    public TodoDTO(TodoEntity todoEntity){

        this.tno = todoEntity.getTno();
        this.title = todoEntity.getTitle();
        this.writer = todoEntity.getWriter();
        this.complete = todoEntity.isComplete();
        this.dueDate = todoEntity.getDueDate();
    }
    // DTO -> Entity메서드
    public TodoEntity toEntity(){
        return TodoEntity.builder()
            .tno(this.tno)
            .title(this.title)
            .writer(this.writer)
            .complete(this.complete)
            .dueDate(this.dueDate)
            .build();
    }



}
