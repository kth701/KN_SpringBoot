package com.example.mallapi.mall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@ToString
public class MemberDTO extends User {

    private String email;
    private String pw;
    private String nickname;
    private boolean del;
    private boolean social;
    private List<String> roleNames = new ArrayList<>();



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
        super(
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
    }
}
