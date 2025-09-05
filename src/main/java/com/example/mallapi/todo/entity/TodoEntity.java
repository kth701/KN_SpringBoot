package com.example.mallapi.todo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_todo")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 100, nullable = false)
    private String writer;

    private boolean complete;

    private LocalDate dueDate;

    public void changeTitle(String title) { this.title = title; }
    public void changeComplete(boolean complete) { this.complete = complete; }
    public void changeDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}


/*
 *  Spring Data JPA
 * JPA => Java persistence API의 약자로 Java 언어에서 지정한 객체의 '영속성 관리'
 * 연속성 관리 =>ORM(Object-Relational Mapping)패러다임
 *  : 객체지향 구조를 관계형 데이터베이스에 매핑해서 관리하는 방식
 * JPA는 Java언어에서 선택한 ORM스펙을 의미( JDK도 하나의 스펙
 * 
 * JPA는 다양한 구현체가 존재하는데 스프링부트는 Hibernate라이브러리를 선택
 * 
 * JPA에서 리포지토리 인터페이스 : CRUD, 페이징 처리 기능을 지원
 * 
 * 
 * 
 */