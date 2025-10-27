package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import com.example.mallapi.mall.dto.MemberDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface MemberService {
    public Member saveMember(MemberDTO memberDTO);

    // default통해 본체 있는 인터페이스 정의
    // 1.  dtoToEntity: MemberDto -> 암호화 -> Entity
    default Member dtoToEntity(MemberDTO memberDTO, PasswordEncoder passwordEncoder){
        Member member = new Member();

        member.setEmail(memberDTO.getEmail());
        // 1. 비밀 번호 -> entity : Security 미적용시 사용
        // member.setPassword(memberDTO.getPassword());

        // 2. 비밀 번호 -> 암호화작업 -> entity:
        // CustomSecurityConfig에서 BCryptPasswordEncoder객체가 생성된 상태
        String password = passwordEncoder.encode(memberDTO.getPw());
        member.setPw(password);
        member.setNickname(memberDTO.getNickname());

        member.addRole(MemberRole.USER);// 권한 Role이 1개이상 경우 (List구조에 저장)
        //member.addRole(MemberRole.MANAGER);
        //member.addRole(MemberRole.ADMIN);

        return member;
    }
}
