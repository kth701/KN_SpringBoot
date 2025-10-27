package com.example.mallapi.mall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
주의: 입력폼 MemberFormDTO와 MemberDTO구분됨
MemberFormDTO: 클라이언트로 부터 회원 가입 정보 전달용
MemberDTO  Security로그인 처리를 위한 User객체 상속받아 처리하는 DT
 */
@Getter@Setter@ToString
//public class MemberDTO extends User  implements OAuth2User { // 1-1. 소셜 로그인 경우
public class MemberDTO extends User {
    // 주의: CustomSecurityConfig클래스에서 주입에서 주입해야함.
    // MemberDTO클래스 생성된 객체는 User객체임을 의미
    // User객체: DB의 정보를 가져와 스프링 시큐리티 세션 정보로 사용하는 것이 목적

    private String email;
    private String pw;

    private String nickname;
    private boolean del;
    private boolean social;
    private List<String> roleNames = new ArrayList<>();

    private Map<String ,Object> props; // 소셜 로그인 정보



    // 생성자
    public MemberDTO(
                            String username,
                            String password,
                            String nickname,
                            boolean social,
                            boolean del,
                            List<String> roleNames
                                     ) {
        // Spring Security의 User 클래스 생성자 호출
        // User객체 스프링 시큐리티에서 사용한 세선정보 객체
        super(// User(부모객체) 생성자를 통해서 username(email), password, 권한설정값 을 초기화
                username,
                password,
                // security권한 역할 다룰때 접두사로 붙이는 규칙
                // "ROLE_"+"USER" => "ROLE_USER"-> security가 인식하는 권한 전환 (ROLE_USER,ROLE_MANAGER, ROLE_ADMIN)
                roleNames.stream()
                        .map( str ->
                                new SimpleGrantedAuthority("ROLE_"+str))
                        .collect(Collectors.toList()));

        // 추가적인 사용자 정보 초기화
        this.email = username;
        this.pw = password;
        this.nickname = nickname;
        this.social = social;
        this.del = del;
        this.roleNames = roleNames;//"USER","MANAGER","ADMIN"
    }

    // JWT문자열 생성시 사용하기 위함
    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("nickname", this.nickname);
        dataMap.put("social", this.social);
        dataMap.put("del", this.del);
        dataMap.put("roleNames", roleNames);

        return dataMap;

        /*
        return Map.of(
                "email", getUsername(),
                "nickname", nickname,
                "social", social,
                "del", del,
                "roleNames", roleNames
        );
        */
    }

    // 1-2. 소셜 로그인 OAuth2 추가시 적용
    /*
    @Override
    public String getName() {
        //return this.email; // 로그인 계정(이메일) 정보 반환
        return getUsername(); // 로그인 계정(이메일) 정보 반환
    }
//    @Override
//    public String getUsername() {
//        return this.email; // 로그인 계정(이메일) 정보 반환
//    }

    @Override
    public Map<String, Object> getAttributes() {
        //return this.getProps(); // 소설 로그인 정보 Map구조로 반환
        return this.props; // 소설 로그인 정보 Map구조로 반환
    }
    */

}
