package com.example.mallapi.mall.security;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/*
Security에서 사용자의 인증 처리하기 위한 인터페이스의 구현
Security 로그인시 :  스프링 시큐리티 정보(principal.username,...) 와 DB정보 연동
=> DB의 정보를 가져와 스프링 시큐리티 세션 정보로 사용하는 것이 목적
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("-----------loadUserByUsername------------" + username);

        Member member = memberRepository.getWithRoles(username);

        if(member == null){
            throw new UsernameNotFoundException("Not Found");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.isDel(),
                // "USER", "MANAGER", "ADMIN"
                member.getMemberRolesList()
                        .stream()
                        .map(memberRole ->
                                memberRole.name())
                        .collect(Collectors.toList())
        );
        log.info(memberDTO);
        return memberDTO;
    }

}
