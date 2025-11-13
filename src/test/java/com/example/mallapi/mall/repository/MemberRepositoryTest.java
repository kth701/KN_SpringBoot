package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsertMember() {
        log.info("---------testInsertMember-----------");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder()
                    .email("user" + i + "@test.com")
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("USER" + i)
                    .social(false)
                    .del(false)
                    .build();

            // 회원 등록시 createdBy, modifiedBy 'anonymousUser'로 자동 초기화됨 -> 테스트용
            //member.setCreatedBy("anonymousUser"); // 인위적으로 초기화 안됨
            //member.setModifiedBy("anonymousUser");

            //  날짜 초기값 설정
            member.setRegTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());

            // role 초기값 설정
            member.addRole(MemberRole.USER);

            if (i >= 5 && i <= 7) { // 3명
                member.addRole(MemberRole.MANAGER);
            }

            if (i >= 8) { // 3명
                member.addRole(MemberRole.MANAGER);
                member.addRole(MemberRole.ADMIN);
            }

            memberRepository.save(member);
        });
    }

    @Test
    public void testUpdateMember() {
        log.info("---------testUpdateMember-----------");

        String email = "user1@example.com";
        String newNickname = "Updated User";
        String newPw = "54321";
        boolean newSocial = true;

        // Ensure the member exists from the insert test or create it
        Member member = memberRepository.findById(email).orElseGet(() -> {
            Member newMember = Member.builder()
                    .email(email)
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("Test User")
                    .social(false)
                    .build();
            newMember.addRole(MemberRole.USER);
            return memberRepository.save(newMember);
        });


        member.changeNickname(newNickname);
        member.changePw(passwordEncoder.encode(newPw));
        member.changeSocial(newSocial);

        memberRepository.save(member);

        Member updatedMember = memberRepository.findById(email).orElseThrow();

        assertEquals(newNickname, updatedMember.getNickname());
        assertTrue(passwordEncoder.matches(newPw, updatedMember.getPw()));
        assertEquals(newSocial, updatedMember.isSocial());
    }


    @Test
    void getWithRoles() {
        Member member = memberRepository.findById("user9@example.com").orElseThrow();

        log.info("=======================");
        log.info(member);
        log.info(member.getMemberRolesList());
    }
}
