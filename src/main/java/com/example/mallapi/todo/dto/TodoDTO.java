package com.example.mallapi.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TodoDTO {
    private Long tno;
    private String title;
    private String writer;
    private boolean complete;

    // "2025-9-4"과  같은 포맷으로 구성
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String dueDate; // 날짜를 문자열로 처리...

}
