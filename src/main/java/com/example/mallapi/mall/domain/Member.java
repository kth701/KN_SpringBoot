package com.example.mallapi.mall.domain;

import com.example.mallapi.mall.dto.MemberDTO;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRolesList")
public class Member extends BaseEntity {

    @Id
    private String email;

    private String pw;
    private String nickname;

    private boolean del; // 회원 탈퇴여부
    private boolean social; // 소셜 로그인 사용 여부

    @ElementCollection(fetch = FetchType.EAGER) // 즉시 연결
    @Builder.Default
    private List<MemberRole> memberRolesList = new ArrayList<>();

    // 권한 부여 메서드
    public void addRole(MemberRole memberRole) {
        memberRolesList.add(memberRole);
    }

    // 권한 해제 메서드
    public void clearRole() {
        memberRolesList.clear();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePw(String pw) {
        this.pw = pw;
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

    public void changeDel(boolean del) {
        this.del = del;
    }

    /**
     * MemberDTO를 Member 엔티티로 변환하는 정적 팩토리 메소드 구현
     * @param memberDTO 회원 정보 DTO
     * @param passwordEncoder 비밀번호 암호화기
     * @return Member 엔티티
     * 현재 날짜로 설정 (등록날짜, 수정날짜), anonymousUser로  등록자, 수정자 자동 초기화 처리됨
     */
    public static Member createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(memberDTO.getEmail())
                .pw(passwordEncoder.encode(memberDTO.getPassword())) //비밀 번호 -> 암호화작업 -> entity:
                //.name(memberDTO.getName()) // 추후 Member Entity항목에 추가 할 경우
                .nickname(memberDTO.getNickname())
                .social(false)
                .del(false)
                .memberRolesList(new ArrayList<>(List.of(MemberRole.USER))) // USER 권한 기본 설정
                .build();
        //.memberRoleList(new arrayList<>( List.of(Role.USER, Role.MANAGER, Role.ADMIN))) // jdk 9부터지원
    }

}
