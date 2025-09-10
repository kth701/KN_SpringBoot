package com.example.mallapi.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.annotation.Transactional;

import com.example.mallapi.todo.dto.PageRequestDTO;
import com.example.mallapi.todo.dto.PageResponseDTO;
import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.entity.TodoEntity;
import com.example.mallapi.todo.service.TodoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;


import org.springframework.transaction.annotation.Propagation;



@SpringBootTest // 통합테스트 용도
// @DataJpaTest // 단위테스트 용도(JPA Entity: 데이터 테스트용)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB에 반영
@Transactional(propagation = Propagation.NOT_SUPPORTED) // 트랜젝션 메서드 단위로 설정 하지 않음
@Log4j2
public class TodoRepositoryTests {

    @Autowired// 객체 주입(테스트에서 주로 사용하는 주입 어노테이션)
    private TodoRepository todoRepository;// todoRepository 객체 생성
    @Autowired
    private TodoService todoService;


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

    // 100 개 데이터 추가
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

            TodoEntity savedEntity = todoRepository.save(todoEntity); // todoRepository를 사용하여 데이터 저장

        }
    }


    // Todo 데이터 조회
    @Test
    @DisplayName("존재하는 TNO로 Todo 데이터 조회")
    public void testTodoRead(){
         Long tno = 1000L;

         // 조회실패시 null처리 -> null 처리하는 타입 -> Optinal
         Optional<TodoEntity> result = todoRepository.findById(tno); // tno 조회요청
        
        TodoEntity todoEntity = result.orElseThrow(); //결과값 반환
        // TodoEntity todoEntity = result.orElseThrow(
        //     () -> new EntityNotFoundException("Todo "+ tno +"를 찾을 수 없습니다.")
        // );


          log.info("==========");
          log.info("==> founded tno: "+ todoEntity.getTno());
          log.info("==> founded todo: {}", todoEntity);
          log.info("==========");

    }

    // Todo  수정
    @Test
    @DisplayName("존재하는 TNO로 Todo 데이터 수정")
    public void testTodoUpdate(){
        // 조회 -> 값 수정 -> 저장
        Long tno = 1L;
        
        Optional<TodoEntity> result = todoRepository.findById(tno);
        // 방법1.
        TodoEntity todoEntity = result.orElseThrow();// 예외가 발생하면 예외발생처리
        // 방법2.
        // TodoEntity todoEntity = result.get();

        todoEntity.changeTitle("Modified 2.....");
        todoEntity.changeComplete(true);
        todoEntity.changeDueDate(LocalDate.of(2025,9, 5));

        // updatedEntity객체가  null아니면 정상 수정
        TodoEntity updatedEntity = todoRepository.save(todoEntity);
        log.info("==> updatedEntity: {}", updatedEntity);

        // 기대값과 결과값이 같으면 true,  그렇지 않으면 false
        assertThat(updatedEntity.getTitle()).isEqualTo("Modified 1.....");

    }
    // Todo 삭제
    @Test
    @DisplayName("존재하는 TNO로 Todo 데이터 삭제")
    public void testTodoDelete(){
        Long tno = 1L;

        todoRepository.deleteById(tno);

    }

    // 페이징 처리: finall()을 이용하는 방식
    @Test
    @DisplayName("페이징 처리:findAll()을 이용하는 방식")
    public void testPaging(){
        // 시작 페이지 : 0부터 시작
        // 검색해서 가져올 페이지 번호, 1페이지에 가져올 레코드 개수, 정렬 기준
        Pageable pageable = PageRequest.of(
                    0, //  검색해서 가져올 페이지 번호
                    10, //  1페이지에 가져올 레코드 개수
                    Sort.by("tno").descending() // 정렬 기준
                    );

        Page<TodoEntity> result = todoRepository.findAll(pageable);

        log.info("-------------------");
        log.info("TotoalElements: {}", result.getTotalElements());
        log.info("TotoalPages: {} ", result.getTotalPages());

        log.info("-------------------");
        result.getContent().stream().forEach(todo -> log.info("==> {}", todo));
        log.info("-------------------");

    }

    @Test
    @DisplayName("페이징 처리: @Query를 이용하는 방식")
    public void testPaging2(){
                Pageable pageable = PageRequest.of( 0, 10, Sort.by("tno").descending() );
                Page<TodoEntity> result = todoRepository.listAll(pageable);

                log.info("==> @Query를 이용하는 방식: {}",result.getContent());
                
                log.info("--------------------------------------");
                result
                    .getContent()
                    .stream()
                    .forEach(todo -> log.info("==> {}", todo));
    }
    @Test
    @DisplayName("페이징 처리: @Query어노테이션 속성")
    public void testPaging3(){
        String keyword = "1";

        Pageable pageable = PageRequest.of( 0, 10, Sort.by("tno").descending() );
        Page<TodoEntity> result = todoRepository.listOfTitle(keyword, pageable);

        log.info("==> @Query어노테이션 속성: {}",result.getContent());
        
        log.info("--------------------------------------");
        result
            .getContent()
            .stream()
            .forEach(todo -> log.info("==> {}", todo));
    }

    @Test
    @DisplayName("Search1 테스트: Querydsl적용")
    public void testSearch1() {
          Pageable pageable = PageRequest.of( 0, 10, Sort.by("tno").descending() );

          Page<TodoEntity> result = todoRepository.search1(pageable);
          result.getContent()
            .stream()
            .forEach(todo -> log.info("==> {}", todo));
    }

    @Test
    @DisplayName("Search2 테스트: Querydsl적용")
    public void testSearch2() {
        Pageable pageable = PageRequest.of( 0, 10, Sort.by("tno").descending() );

        Page<TodoDTO> result = todoRepository.searchDTO(pageable);
        result.getContent()
          .stream()
          .forEach(todo -> log.info("==> {}", todo));

    }

    @Test
    @DisplayName("DTO를 이용한 등록 테스트")
    public void testServiceRegister() {

        TodoDTO todoDTO = new TodoDTO();

        todoDTO.setTitle("Test Todo");
        todoDTO.setWriter("user00");
        todoDTO.setComplete(false);
        todoDTO.setDueDate(LocalDate.now().plusDays(1));

        // Service테스트: 통합테스트 => @SpringBootTest적용
        // Repository테스트: 단위테스트 => @DataJpaTest적용시 에러
        TodoDTO savedTodoDTO = todoService.register(todoDTO);

        log.info("--- savedTodoDTO: {}", savedTodoDTO);

        assertThat(savedTodoDTO.getTno()).isNotNull();

    }

    @Test
    @DisplayName("DTO를 이용한 조회 테스트")
    public void testGetTodoDTO() {
        Long tno = 1L;
        //  Long tno = 1000L; // 업는 tno번호 조회시 사용자가 정의 예외 발생 처리
        TodoDTO result = todoService.read(tno);

        log.info("==> result: {}", result);

        assertThat(result.getTno()).isEqualTo(tno);
        
    }
    @Test
    @DisplayName("DTO를 이용한 삭제 테스트")
    public void testRemoveTodoDTO() {
        // Long tno = 1000L;// 존재하지 않는 tno
        Long tno = 105L; // 존재하는 tno
        todoService.remove(tno);

    }   
    @Test
    @DisplayName("DTO를 이용한 수정 테스트")
    public void testModifyTodoDTO(){
        // 수정할 객체 생성해서 초기화
        TodoDTO todoDTO = new TodoDTO();

        todoDTO.setTno(104L);
        todoDTO.setTitle("수정된 제목");
        todoDTO.setComplete(true);
        todoDTO.setDueDate(LocalDate.now().plusDays(1));
        
        // 수정 서비스 요청
        TodoDTO result = todoService.modify(todoDTO);
        log.info("==> result: {}", result);

        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.isComplete()).isEqualTo(true);
        assertThat(result.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));

    }

    @Test
    @DisplayName("DTO를 이용한 목록 조회 테스트")
    public void testListTodoDTO(){
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        // 기본값: page=1, size=10

        pageRequestDTO.setPage(2);
        pageRequestDTO.setSize(5);

        Page<TodoDTO> result = todoService.list(pageRequestDTO);

        log.info("-----------------------");
        log.info("Prev: {}", result.previousPageable());
        log.info("Next: {}", result.nextPageable());
        log.info("Total: {}", result.getTotalElements());
        log.info("Number: {}", result.getNumber());
        log.info("-----------------------");
        log.info("==> result: {}", result);
        log.info("-----------------------");

        result.getContent()
            .stream()
            .forEach(todo -> log.info("=> {}", todo));

    }

    @Test
    @DisplayName("PageResponseDTO 이용한 목록 조회 테스트2")
    public void testGetTodoList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
            .page(2)
            .size(10)
            .build();

        PageResponseDTO<TodoDTO> result = todoService.getTodoList(pageRequestDTO);
        
        log.info("--- PageResponseDTO 이용한 목록 조회 테스트");
        log.info("==> result: {}", result);


    }
        








        



}
