package com.example.mallapi.todo.exception;

import lombok.Getter;
import lombok.ToString;

@Getter@ToString
public class EntityNotFoundException extends RuntimeException {
    private String messge;
    private int code;

    public EntityNotFoundException(String message, int code) {
        super(message);

        this.messge = message;
        this.code = code;
    }

}
