package com.example.mallapi.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.transaction.annotation.Transactional;

import com.example.mallapi.todo.entity.TodoEntity;

import lombok.extern.log4j.Log4j2;


import org.springframework.transaction.annotation.Propagation;



// @SpringBootTest // 통합테스트 용도
@DataJpaTest // 단위테스트 용도(JPA Entity: 데이터 테스트용)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB에 반영
@Transactional(propagation = Propagation.NOT_SUPPORTED) // 트랜젝션 메서드 단위로 설정 하지 않음
@Log4j2
public class TodoRepositoryTests {

    @Autowired// 객체 주입(테스트에서 주로 사용하는 주입 어노테이션)
    private TodoRepository todoRepository;// todoRepository 객체 생성


    @Test
    @DisplayName("Todo Insert Test")
    public void testInsert() {
        TodoEntity todoEntity = TodoEntity.builder()
                    .title("스프링부트 공부 끝내기")
                    .writer("user00")
                    .complete(false)
                    .dueDate(LocalDate.of(2025, 9,4))
                    .build();

        log.info("DB 저장 전 TodoEntity: {}", todoEntity);
        TodoEntity savedEntity = todoRepository.save(todoEntity);// 영속성 컨텍스트에 반영

        log.info("DB 저장 후 TodoEntity: {}", savedEntity);

        assertThat(savedEntity.getTno()).isNotNull(); // 저장 후 tno가 생성되었는지 확인
    }

    @Test
    @DisplayName("더미 데이터 100개 추가")
    public void testInsertDummies() { // testInsertDummies() 메서드
        for (int i = 1; i <= 100; i++) { // for문으로 변경
            TodoEntity todoEntity = TodoEntity.builder() // TodoEntity 객체 생성
                .title("Dummy Title " + i) // title 설정
                .writer("user" + (i % 10)) // writer 설정 (10명으로 순환)
                .complete(false) // complete 설정
                .dueDate(LocalDate.now().plusDays(i)) // dueDate 설정 (오늘부터 하루씩 증가)
                .build(); // 빌더 패턴으로 객체 생성
            todoRepository.save(todoEntity); // todoRepository를 사용하여 데이터 저장
        }
    }



}
