package com.example.mallapi.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mallapi.todo.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    // 사용자가 원하는  쿼리 메서드 작성
}
