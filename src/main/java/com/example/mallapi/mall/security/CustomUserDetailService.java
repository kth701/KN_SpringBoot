package com.example.mallapi.mall.security;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.exception.member.MemberExceptions;
import com.example.mallapi.mall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/*
Security 로그인 에서 사용자의 인증 처리하기
    - UserDetailsService인터페이스를 직접 구현(현재 CustomUserDetailService클래스에서 적용)
    - 주의: CustomSecurityConfig클래스에서  CustomUserDetailService클래스 객체 주입해야함.
             스프링 시큐리티에서 사용하는  세선 정보 객체로 등록한다는 의미


Security 로그인시 :  스프링 시큐리티 정보(principal.username,...) 와 DB정보 연동
=> DB의 정보를 가져(UserDetailsService역할)와 스프링 시큐리티 세션 정보로 사용하는 것이 목적
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 로그인 성공시 회원정보 DB정보를 memberDTO저장 및 세션(session) 객체 등록
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("-----------loadUserByUsername------------" + username);

        // 1. 더미 User객체 생성
        /*
            //private final PasswordEncoder passwordEncoder;// 테스트할 경우만 적용 (순환반복현상 발생)
            UserDetails userDetails = User.builder()
                    .username("admin@email.com")
                    //.password(passwordEncoder.encode("1111"))
                    .password("1111")
                    .authorities("ROLE_USER")
                    .build();
            return userDetails; */


            // http://localhost:8099/login =>로그인 절차거치면 설정됨.
            /*
            // 1. 더미 User객체 생성 :
            UserDetails userDetails = User.builder()
                    .username("user1@email.com")
                    .password(passwordEncoder.encode("1111"))
                    .authorities(Role.USER.toString())
                    .build();
            log.info("=>userDetails: "+userDetails.toString());
        */

        // 2. DB로 회원 정보 가져와  User객체 생성
        /*
        Member member = memberRepository.findByEmail(username);
        if (member==null) throw new UsernameNotFoundException(username);

        UserDetails userDetails = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
        log.info("=>userDetails: "+userDetails.toString());

        return userDetails;
         */


        //  시큐리티 로그인에서 전달받은 username을 통해 Member Entity 데이터 가져오기
        Member member = memberRepository.getWithRoles(username);
        if(member == null){
            //throw new UsernameNotFoundException("Not Found");
            throw MemberExceptions.NOT_FOUND.get(); // 수정후
        }

        // User클래스로 부터 상속받은 MemberSecurityDTO
        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.isDel(),
                // 회원 Role, getName()=>USER(0), MANAGER(1), ADMIN(2) 숫자대신 문자열
                // "USER", "MANAGER", "ADMIN"
                member.getMemberRolesList()
                        .stream()
                        .map(Enum::name).collect(Collectors.toList())
                         /*
                        //또는
                        .map(memberRole ->
                                memberRole.name()).collect(Collectors.toList())

                        */
        );
        log.info(memberDTO);

        // UserDetails 인터페이스(상위클래스), UserDetails인터페이스 구현한 클래스는 User클래스(자식)
        // User객체는 스프링 시큐리티에서 사용하는  세선 정보 객체로 생성자를 통해 데이터를 전달받음=>MemberDTO클래스에서 직접 구현
        // User클래스 객체 반환(memberDTO객체는 User클래스부터 상속 받은 객체로 User객체 타입으로 반환 가능)
        //  => 스프링 시큐리티에서 사용하는  세선 정보 객체로 등록
        //  => MemberDTO생성자로 통해서 username, password 세선 정보로 등록후 로그인 폼에서 넘어온 email, password와 비교하여 로그인 성공 여부 결정)
        return memberDTO;


        /* MemberDTO클래스에서 User클래스를 상속받지 않고 User클래스 객체 생성자로 통해 UserDetails객체를 생성하기
        // User객체는 스프링 시큐리티에서 사용하는  세선 정보 객체로 생성자를 통해 데이터를 전달받음=>MemberDTO클래스에서 직접 구현
        return User.builder()
                    .username(member.getEmail())
                    .password(member.getPw())
                    .roles(member.getMemberRolesList().toString())
                    .build();

         */
    }

}


/*
스프링 시큐리터를 이용하여 로그인/로그아웃 기능 구현

- 스프링 시큐리티 정보(principal.username,...) 와 DB정보 연동
- UserDetailService 인터페이스 : 데이터베이스에서 회원 정보를 가져오는 역할
- loadUserByuUsername()메소드 통해, 회원 정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetails인터페이스를 반환
- User클래스는 UserDetails인터페이스를 구현하고 있는 클래스

 ==> DB의 정보를 가져와 스프링 시큐리티 세션 정보로 사용하는 것이 목적

 */
