package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface MemberService {

    // 회원등록
    //public Member saveMember(MemberDTO memberDTO); // 수정전
    public Member saveMember(MemberFormDTO memberFormDTO); // 수정후

    // --------------------------------------------------------- //
    // default키워드를 통해 본체 있는 인터페이스 정의하기
    // --------------------------------------------------------- //

    // 0. memberFormDTO -> securityMemberDTO변환
    default MemberDTO memberFormToMemberDTO(MemberFormDTO memberFormDTO){
        return new MemberDTO(
                memberFormDTO.getEmail(),
                memberFormDTO.getPw(),
                memberFormDTO.getNickname(),
                false,
                false,
                List.of("USER")
        );
    }

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
    default MemberDTO entityToMemberDTO(Member member){
        return new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.isDel(),
                member.getMemberRolesList().stream().map(Enum::name).toList() // 참조 메서드
                // member.getMemberRolesList().stream().map(memberRole -> memberRole.name()).toList()
        );
    }
    default MemberFormDTO memberDTOtoForm(MemberDTO memberDTO) {
        MemberFormDTO memberForm = new MemberFormDTO();
        memberForm.setEmail(memberDTO.getEmail());
        memberForm.setPw(memberDTO.getPw());
        memberForm.setNickname(memberDTO.getNickname());
        memberForm.setSocial(memberDTO.isSocial());
        memberForm.setDel(memberDTO.isDel());
        return memberForm;


    }

    // 회원 조회
    public MemberFormDTO findMember(String email);

    // 회원 수정
    public Member updateMember(MemberFormDTO memberFormDTO);

    // 회원 삭제
//    public void deleteMember(String email);

    // 회원 목록
    List<MemberFormDTO> getAdminMemberPage(MemberSearchDTO memberSearchDTO, Pageable pageable);

}
