package com.example.mallapi.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mallapi.todo.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    // 사용자가 원하는  쿼리 메서드 작성

    // @Query를 이용하는 방식( select * from tbl_todo )
    @Query("select t from TodoEntity t")
    Page<TodoEntity> listAll(Pageable pageable);

    // @Query 어노테이션의 속성 활용=> ":매개변수"
    @Query( "select t from TodoEntity t " +
                    " where t.title " +
                     " like %:keyword%  and t.tno > 0 " +
                    // " like concat('%', :keyword, '%') and t.tno > 0 " +
                    " order by t.tno desc")
    // @Query( value = "select * from tbl_todo t where t.title like concat('%', :keyword, '%')",
    //         countQuery = "select count(*) from tbl_todo t where t.title like concat('%', :keyword, '%')",
    //         nativeQuery = true)
    Page<TodoEntity>listOfTitle(@Param("keyword") String keyword, Pageable pageable);
}
