package com.example.mallapi.todo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.mallapi.todo.dto.PageRequestDTO;
import com.example.mallapi.todo.dto.PageResponseDTO;
import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.entity.TodoEntity;
import com.example.mallapi.todo.repository.TodoRepository;

import jakarta.persistence.EntityNotFoundException;
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

    // 조회 기능 구현
    @Override
    public TodoDTO read(Long tno) {

        // Repository에서는 Entity타입으로 반환 -> 자동 DTO 변환
        Optional<TodoDTO> result = todoRepository.getDTO(tno);
        TodoDTO todoDTO = result.orElseThrow(
            // 사용자가 직접 작성한 예외 처리 객체 생성하여 처리
            ()-> new EntityNotFoundException("Todo "+ tno +"를 찾을 수 없습니다.")
        );

        return todoDTO;

    }

    @Override
    public void remove(Long tno) {
        // 삭제할 tno 존재 여부 확인
        Optional<TodoEntity> result = todoRepository.findById(tno);
        // 없으면 예외 처리 발생 처리
        TodoEntity todoEntity = result.orElseThrow(
            ()->new EntityNotFoundException("삭제할 "+ tno +" 를 찾을 수 없습니다")
        );

        // 있으면 삭제 처리
        todoRepository.delete(todoEntity);
        //todoRepository.deleteById(tno);
    }

    @Override
    public TodoDTO modify(TodoDTO todoDTO) {
        // 수정할 tno 존재 여부 확인
        Optional<TodoEntity> result = todoRepository.findById(todoDTO.getTno());

        TodoEntity todoEntity = result.orElseThrow(
            () ->new EntityNotFoundException("수정할 "+ todoDTO.getTno() +" 를 찾을 수 없습니다")
        );

        //  수정 작업 처리
        todoEntity.changeTitle(todoDTO.getTitle());
        todoEntity.changeComplete(todoDTO.isComplete());
        todoEntity.changeDueDate(todoDTO.getDueDate());

        // 트랜잭션 내에서 실행 -> 변경 감지를 이용 -> save()
        // todoRepository.save(todoEntity); // 생략하면 자동 save

        return new TodoDTO(todoEntity);
    }

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {

        Sort sort = Sort.by("tno").descending();
        Pageable pageable = pageRequestDTO.getPageable(sort);

        return todoRepository.searchDTO(pageable);
    }

    @Override
    public PageResponseDTO<TodoDTO> getTodoList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
            pageRequestDTO.getPage()-1, // 보여질 페이지 번호
            pageRequestDTO.getSize(), // 한페이지에 보여질 데이터 개수
            Sort.by("tno").descending()); // 정렬 기준


        // query 결과값 Entity타입
        Page<TodoEntity> result = todoRepository.findAll(pageable);

        // ModelMapper이용 : Entity -> DTO
        List<TodoDTO> dtoList = result.getContent().stream()
        // map(ModelMapper을 이용하여 entity -> dto)
            .map(entity -> modelMapper.map(entity, TodoDTO.class)) 
            .collect(Collectors.toList()); // stream에 있는 객체를 List타입으로 변환

        long totalCount = result.getTotalElements();// xxx.getTotalElements() :long 타입

        PageResponseDTO<TodoDTO> pageResponseDTO = PageResponseDTO.<TodoDTO>withAll()
            .dtoList(dtoList)  // 쿼리 결과 값은 전달
            .pageRequestDTO(pageRequestDTO) // 페이지 정보 전달
            .totalCount(totalCount) // 전체 데이터 전달
            .build();

        return pageResponseDTO;


    }

}
