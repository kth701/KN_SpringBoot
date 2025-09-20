package com.example.mallapi.mall.repository;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsertMember() {
        log.info("---------testInsertMember-----------");

        Member member = Member.builder()
                .email("test@example.com")
                //.pw(passwordEncoder.encode("1111"))
                .pw("1111")
                .nickname("Test User")
                .social(false)
                .build();

        member.addRole(MemberRole.USER);

        memberRepository.save(member);
    }

    @Test
    public void testUpdateMember() {
        log.info("---------testUpdateMember-----------");

        String email = "test@example.com";
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
        Member member = memberRepository.findById("test@example.com").orElseThrow();

        log.info("=======================");
        log.info(member);
        log.info(member.getMemberRoles());
    }
}
