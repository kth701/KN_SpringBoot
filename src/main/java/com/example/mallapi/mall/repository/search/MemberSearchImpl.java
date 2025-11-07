package com.example.mallapi.mall.repository.search;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.QMember;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 정보에 대한 동적 쿼리 및 검색을 처리하는 QueryDSL 구현체 클래스.
 * - MemberSearch 인터페이스 구현.
 * - 복잡한 검색 조건(가입일, 검색 유형, 검색어 등)을 동적으로 생성하여 처리.
 */
public class MemberSearchImpl implements MemberSearch {

    // QueryDSL 쿼리를 생성하고 실행하기 위한 핵심 객체
    private final JPAQueryFactory queryFactory;

    /**
     * 생성자 주입(Constructor Injection)을 통해 EntityManager를 주입받음.
     * 주입받은 EntityManager를 사용하여 JPAQueryFactory를 초기화.
     * @param em 영속성 컨텍스트를 관리하는 EntityManager 객체
     */
    public MemberSearchImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 가입일 필터링 조건을 생성하는 private 헬퍼 메소드.
     * searchDateType 값에 따라 다른 시간 간격의 WHERE 조건을 동적으로 생성.
     *
     * @param searchDateType 검색 기간 타입 (예: "1d", "1w", "1m", "6m")
     * @return 생성된 BooleanExpression (QueryDSL의 WHERE절 조건). 조건이 없으면 null 반환.
     */
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now(); // 현재 시간을 기준으로 설정

        // "all" 또는 null/빈 문자열인 경우, 기간 필터링을 적용하지 않음 (null 반환)
        if (StringUtils.isEmpty(searchDateType) || StringUtils.equals("all", searchDateType)) {
            return null;
        }

        // 기간 타입에 따라 dateTime을 과거로 설정
        if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        // QMember.member.regTime이 계산된 dateTime 이후인 조건(BooleanExpression)을 반환
        return QMember.member.regTime.after(dateTime);
    }

    /**
     * 검색어 필터링 조건을 생성하는 private 헬퍼 메소드.
     * searchBy 값(email, nickname)에 따라 해당 필드에서 LIKE 검색을 수행.
     *
     * @param searchBy 검색할 필드 (예: "email", "nickname")
     * @param searchQuery 검색어
     * @return 생성된 BooleanExpression. 검색어가 없으면 null 반환.
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        // 검색어가 비어있거나 null이면, 검색 조건을 적용하지 않음 (null 반환)
        if (StringUtils.isEmpty(searchQuery)) {
            return null;
        }

        // 검색 기준(searchBy)에 따라 다른 필드를 검색
        if (StringUtils.equals("email", searchBy)) {
            return QMember.member.email.like("%" + searchQuery + "%"); // email 필드에서 LIKE 검색
        } else if (StringUtils.equals("nickname", searchBy)) {
            return QMember.member.nickname.like("%" + searchQuery + "%"); // nickname 필드에서 LIKE 검색
        }

        // 유효한 searchBy 값이 아니면 조건을 적용하지 않음
        return null;
    }

    /**
     * 관리자 페이지에서 사용할 회원 목록을 동적 쿼리와 페이징을 적용하여 조회.
     *
     * @param memberSearchDTO 검색 조건들을 담고 있는 DTO
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬 정보)
     * @return 페이징된 회원 목록 (Page<Member>)
     */
    @Override
    public Page<Member> searchMembers(MemberSearchDTO memberSearchDTO, Pageable pageable) {
        QMember member = QMember.member; // Q-Type 클래스 사용

        // 1. 데이터 목록 조회 쿼리 실행
        List<Member> content = queryFactory
                .selectFrom(member) // 회원(Member) 엔티티를 대상으로 쿼리 시작
                .where( // WHERE 절: 동적 조건들을 결합. 조건이 null이면 해당 조건은 무시됨.
                        regDtsAfter(memberSearchDTO.getSearchDateType()), // 가입일 조건
                        searchByLike(memberSearchDTO.getSearchBy(), memberSearchDTO.getSearchQuery()) // 검색어 조건
                )
                .orderBy(member.regTime.desc()) // 가입일 기준 내림차순 정렬
                .offset(pageable.getOffset())   // 페이징: 시작 오프셋 설정 (예: 2페이지 -> 10번째부터)
                .limit(pageable.getPageSize())  // 페이징: 한 페이지에 가져올 데이터 수
                .fetch(); // 쿼리를 실행하고 결과를 List<Member> 형태로 반환

        // 2. 전체 데이터 개수 조회 쿼리 실행 (페이징 처리를 위해 필요)
        Long total = queryFactory
                .select(member.count()) // count(member_id) 쿼리 생성 (성능 최적화)
                .from(member)
                .where( // 데이터 조회와 동일한 조건 적용
                        regDtsAfter(memberSearchDTO.getSearchDateType()),
                        searchByLike(memberSearchDTO.getSearchBy(), memberSearchDTO.getSearchQuery())
                )
                .fetchOne(); // 쿼리를 실행하고 단일 결과(long 타입)를 반환

        // 3. PageImpl 객체 생성 및 반환
        // 조회된 데이터 목록(content), 페이징 요청 정보(pageable), 전체 개수(total)를 사용하여 Page 객체를 생성
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
