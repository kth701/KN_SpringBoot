package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {
    // @EntityGraph => N+1방지및 최적화
//    @Query("select m from Member m join fetch m.memberRolesList where m.email = :email ")
//    Member getWithRoles(@Param("email") String email);

    @EntityGraph(attributePaths = "memberRolesList")
    @Query("select m from Member m where m.email = :email ")
    Member getWithRoles(@Param("email") String email);
}


/*
JPQL의 페치 조인(fetch join)은 연관된 엔티티나 컬렉션을 한 번의 SQL 쿼리로 함께 조회하여 성능을 최적화하는 기능
Member를 조회할 때 memberRolesList도 같이 조회
Repository의 메서드에 @EntityGraph를 붙여 fetch join을 적용

test
 */