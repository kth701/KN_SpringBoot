package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.domain.MemberRole;
import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public interface MemberService {

    // 회원등록
    //public Member saveMember(MemberDTO memberDTO); // 수정전
    public Member saveMember(MemberFormDTO memberFormDTO); // 수정후

    // --------------------------------------------------------- //
    // default키워드를 통해 본체 있는 인터페이스 정의하기
    // --------------------------------------------------------- //

    // 0. memberFormDTO -> securityMemberDTO변환
    default MemberDTO memberFormToMemberDTO(MemberFormDTO memberFormDTO){


        // 가입날짜
        return new MemberDTO(
                memberFormDTO.getEmail(),
                memberFormDTO.getPw(),
                memberFormDTO.getNickname(),

                //  생성자 형식 맞추기 위해 기본값 설정
                memberFormDTO.isSocial(), // 소셜
                memberFormDTO.isDel(),// 탈퇴: 회원가입인 경우 false넘겨받음, 회원수정인 경우 변경된 값 넘겨받음
                memberFormDTO.getRoleNames(), // role -> 주의: MemberDTO클래스 생성자에 stream()통해 순차적으로 데이터 입력
                memberFormDTO.getRegTime());
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

        // 주의
        // MemberFormDTO , MemberDTO 객체에는 role타입이   List<String> , Member  객체에는 role 타입 List<MemberRole>
        member.clearRole(); // 기존에 Member 엔티티에 설정된 모든 역할을 삭제
        memberDTO.getRoleNames().forEach(roleName -> {
            // DTO에서 받은 역할 이름(문자열)을 MemberRole enum으로 변환하여 Member 엔티티에 추가
            member.addRole(MemberRole.valueOf(roleName));
        });

        member.setDel(memberDTO.isDel()); // 탈퇴 여부 변경


        // 관리자모드에서 수정에서 하지않고 직접 설정: 테스트용
        //member.addRole(MemberRole.USER);// 권한 Role이 1개이상 경우 (List구조에 저장)
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
                member.getMemberRolesList().stream().map(Enum::name).toList(),                          // 참조 메서드
                //member.getMemberRolesList().stream().map(memberRole -> memberRole.name()).toList(), // 람다식
                member.getRegTime()
        );
    }
    default MemberFormDTO memberDTOtoForm(MemberDTO memberDTO) {
        MemberFormDTO memberFormDTO = new MemberFormDTO();

        memberFormDTO.setEmail(memberDTO.getEmail());

        memberFormDTO.setSavedPw(memberDTO.getPw()); // 기존 DB에 저장된 비밀번호

        memberFormDTO.setNickname(memberDTO.getNickname());
        memberFormDTO.setSocial(memberDTO.isSocial());
        memberFormDTO.setDel(memberDTO.isDel());
        memberFormDTO.setRegTime(memberDTO.getRegTime());

        memberFormDTO.setRoleNames(memberDTO.getRoleNames());
        return memberFormDTO;


    }

    // 회원 조회
    public MemberFormDTO findMember(String email);

    // 회원 수정
    public Member updateMember(MemberFormDTO memberFormDTO);

    // 회원 삭제
//    public void deleteMember(String email);

    // 회원 목록, Entity-> DTO -> FormEntity 과정에 필요한 여러 데이터 타입 추출
    Map<String, Object> getAdminMemberPage(MemberSearchDTO memberSearchDTO, Pageable pageable);

}
