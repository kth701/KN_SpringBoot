package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import com.example.mallapi.mall.exception.member.MemberExceptions;
import com.example.mallapi.mall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
// 방법1.  User클래스상속받은 MemberDTO클래스에서 구현
public class MemberServiceImpl implements MemberService {
// 방법2. MemberServiceImpl클래스에서 User객체를 구현하는 UserDetailsService인터페이스를  직접 작성
// -> User클래스 생성자를 통해 UserDetails객체 생성하여 반환하여 세션정보를 생성
//public class MemberServiceImpl implements MemberService, UserDetailsService {
    /*
         security패키지 CustomUserDetailService클래스에서
         UserDetailsService구현 클래스는 CustomSecurityConfig클래스 멤버변수 객체 주입하는 방식으로 사용
     */
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper  modelMapper;


    @Override
    public Member saveMember(MemberFormDTO memberFormDTO) {
//    public Member saveMember(MemberDTO memberDTO) {
        // 1.
        //Member member = Member.createMember(memberDTO);

        // 2.1
        MemberDTO memberDTO = memberFormToMemberDTO(memberFormDTO); // memberFormDTO -> MemberDTO(Security User DTO)
        Member member = dtoToEntity(memberDTO, passwordEncoder);     // memberDTO -> Entity

        // 2.2 회원 중복 체크(email)기준
        /*  회원 중복 체크 함수(메서드)로 별로 정의하여 호출  허가
        *  validateDuplicatemember(member);
        * */

        // 방법1.
        Member findMember =  memberRepository.findByEmail(member.getEmail());
        //  자바스크립트에서  e.getMessage()속성 값에 따라른 error message  처리
        if (findMember!=null) throw  MemberExceptions.DUPLICATE.get();
        //if (findMember!=null) throw  new IllegalStateException("DuplicateMember"); // 상태메시지

        /* test
            if (findMember != null) {
                log.info("------------ 중복 체크 확인");
                log.info(findMember.toString());
                throw   MemberExceptions.DUPLICATE.get();
            }
         */

        /*
        // 방법2
        Optional<Member> result = memberRepository.findById(member.getEmail());
        boolean isResult = result.isPresent();
        if (isResult) throw MemberExceptions.DUPLICATE.get();;
         */


        // 2.3 중복된 이메일 없을 경우 저장(반영)
        return memberRepository.save(member);
    }
        /*
        // 회원 중복 체크(email)처리하는 메서드 정의
        // entity -> 이메일 유무체크
        private void validateDuplicatemember(Member member){
            Member findMember =  memberRepository.findByEmail(member.getEmail());
            if (findMember!=null) throw  new IllegalStateException("이미 가입된 회원입니다.");
        }
     */


    // 이메일로 회원 정보 조회 구현
    @Transactional(readOnly = true)
    @Override
    public MemberFormDTO findMember(String email) {
        // 1. 이메일로 회원 정보 조회
//        Member member = memberRepository.findByEmail(email);
//        if (member == null) throw  new IllegalStateException("MemberNotFound");  //  자바스크립트에서  e.getMessage()속성 값에 따라른 error message  처리


        // 방법2.
        Optional<Member> result = memberRepository.findById(email);
        Member member = result.orElseThrow(MemberExceptions.NOT_FOUND::get);


        MemberFormDTO memberFormDTO = new MemberFormDTO();

        memberFormDTO.setEmail(member.getEmail());
        memberFormDTO.setSavedPw(member.getPw()); // 기존 DB에 저장된 비밀번호
        memberFormDTO.setNickname(member.getNickname());

        return memberFormDTO;
    }


    // 회원 정보 수정 구현
    @Override
    public Member updateMember(MemberFormDTO memberFormDTO) {

        MemberDTO memberDTO = memberFormToMemberDTO(memberFormDTO); // memberFormDTO -> MemberDTO(Security User DTO)
        Member member = dtoToEntity(memberDTO, passwordEncoder);     // memberDTO -> Entity

        return memberRepository.save(member);

    }

    // 회원 목록 List
    @Transactional(readOnly = true)
    @Override
    public List<MemberFormDTO> getAdminMemberPage(MemberSearchDTO memberSearchDTO, Pageable pageable) {
        List<MemberFormDTO> members;
        members = memberRepository.searchMembers( memberSearchDTO,  pageable)
//                .map(member -> entityToMemberDTO(member) )
//                .map(memberDTO -> memberDTOtoForm(memberDTO) )
                .map(this::entityToMemberDTO)
                .map(this::memberDTOtoForm)
                .toList();

//        return memberRepository.searchMembers( memberSearchDTO,  pageable);
        return members;
    }







    /*
     --------------------------------------------------------------------------------
        1. security.CustomUserDetailService클래스에서
            UserDetailsService 인터페이스 구현 : 현재 소스에서는  1. 방식 적용중
      --------------------------------------------------------------------------------
     */


    /*  MemberServiceImpl클래스에서 UserDetailsService인터페이스 구현 방식:
            -> DB에서 회원정보를 가져오는와서 User객체 정보로 적용하는 역할

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=>로그인 아이디: "+username);

        // http://localhost:8099/login =>로그인 절차거치면 설정됨.

        // 1. 더미 User객체 생성 :
//        UserDetails userDetails = User.builder()
//                .username("user1@email.com")
//                .password(passwordEncoder.encode("1111"))
//                .authorities(Role.USER.toString())
//                .build();
//        log.info("=>userDetails: "+userDetails.toString());


        // 2. DB로 회원 정보 가져와  User객체 생성
        Member member = memberRepository.findByEmail(username);
        if (member==null) throw new UsernameNotFoundException(username);

        UserDetails userDetails = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
        log.info("=>userDetails: "+userDetails.toString());

        return userDetails;
    }

     */

}



/*
스프링 시큐리터를 이용하여 로그인/로그아웃 기능 구현

- 스프링 시큐리티 정보(principal.username,...) 와 DB정보 연동
- UserDetailService 인터페이스 : 데이터베이스에서 회원 정보를 가져오는 역할
- loadUserByuUsername()메소드 통해, 회원 정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetails인터페이스를 반환
- User클래스는 UserDetails인터페이스를 구현하고 있는 클래스

 ==> DB의 정보를 가져와 스프링 시큐리티 세션 정보로 사용하는 것이 목적

 */
