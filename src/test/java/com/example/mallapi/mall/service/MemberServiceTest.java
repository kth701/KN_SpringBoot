package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberFormDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public MemberFormDTO createMemberFormDTO() {

        return MemberFormDTO.builder()
                .email("testuser1@test.com")
                .pw("1111")
                .nickname("TestUser")
                .social(false)
                .roleNames(List.of("USER"))
                .regTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void saveMemberTest() {
        // given
        MemberFormDTO memberFormDTO = createMemberFormDTO();

        // when
        Member savedMember = memberService.saveMember(memberFormDTO);

        // then
        assertNotNull(savedMember);
        log.info("--> dto(email):{}, member(email){}", memberFormDTO.getEmail(),  savedMember.getEmail());
        log.info("--> dto(nickname):{}, member(nickname){}", memberFormDTO.getNickname(),  savedMember.getNickname());
        log.info("--> dto(pw):{}, member(pw){}", memberFormDTO.getPw(),  savedMember.getPw());
        log.info("--> dto(roleNames):{}, member(roleNames):{}",memberFormDTO.getRoleNames(), savedMember.getMemberRolesList());


    }


    @Test
    @DisplayName("중복 회원 가입 테스트")
    void saveDuplicateMemberTest() {
        MemberFormDTO memberFormDTO1 = createMemberFormDTO();
        MemberFormDTO memberFormDTO2 = createMemberFormDTO();
        memberService.saveMember(memberFormDTO1);

        Throwable e = assertThrows(IllegalStateException.class, ()->{
            memberService.saveMember(memberFormDTO2);// 중복된 회원가입 시도 하여 강제로 예외 발생시킴
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());

    }
}
