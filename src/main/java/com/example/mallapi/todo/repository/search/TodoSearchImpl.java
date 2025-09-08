package com.example.mallapi.todo.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.mallapi.todo.entity.QTodoEntity;
import com.example.mallapi.todo.entity.TodoEntity;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport   implements TodoSearch {

    public TodoSearchImpl(Class<?> domainClass) {
        super(TodoEntity.class);
    }

    @Override
    public Page<TodoEntity> search1(Pageable pageable) {

        log.info("---- search1: Querydsl test");

        // 1. Entity -> QEntity(Q도메인)
        QTodoEntity todoEntity = QTodoEntity.todoEntity;

        // sql -> select * from tbl_todo where tno > 0;
        // @Query어노테이션  -> select t from TodoEntity t where t.tno > 0

        // 2. 쿼리문 구성
        JPQLQuery<TodoEntity> query = from(todoEntity); // ~ from tbl_todo
        query.where(todoEntity.tno.gt(0L)); // ~ where tno > 0

        // 3. 쿼리문 결과값 반환
        getQuerydsl().applyPagination(pageable, query); // 페이징 처리한 쿼리문 실행
        List<TodoEntity> entityList = query.fetch();// 쿼리 결과값 반환

        // 쿼리문 개수
        long count = query.fetchCount(); // 쿼리문에 대한 레코드 개수

        return new PageImpl<>(entityList, pageable, count);
    }

}
