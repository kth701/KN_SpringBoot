package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class MemberServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;


    // 더미 객체 생성하기
    public MemberDTO createMemberDTO() {

        return new MemberDTO(        // 생성자로 통해 객체 생성하기
                "user1@test.com",
                "1111",
                "TestUser",
                false,
                false,
                List.of("USER")
        );
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void saveMemberTest() {
        // given
        MemberDTO   memberDTO = createMemberDTO();

        // when
        Member savedMember = memberService.saveMember(memberDTO);

        // then
        assertNotNull(savedMember);
        log.info("--> dto(email):{}, member(email){}", memberDTO.getEmail(),  savedMember.getEmail());
        log.info("--> dto(nickname):{}, member(nickname){}", memberDTO.getNickname(),  savedMember.getNickname());
        log.info("--> dto(pw):{}, member(pw){}", memberDTO.getPw(),  savedMember.getPw());
        log.info("--> dto(roleNames):{}, member(roleNames):{}",memberDTO.getRoleNames(), savedMember.getMemberRolesList());


    }


    @Test
    @DisplayName("중복 회원 가입 테스트")
    void saveDuplicateMemberTest() {
        MemberDTO memberDTO1 = createMemberDTO();
        MemberDTO memberDTO2 = createMemberDTO();
        memberService.saveMember(memberDTO1);

        Throwable e = assertThrows(IllegalStateException.class, ()->{
            memberService.saveMember(memberDTO2);// 중복된 회원가입 시도 하여 강제로 예외 발생시킴
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());

    }
}
