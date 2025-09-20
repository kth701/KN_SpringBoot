package com.example.mallapi.mall.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRolesList")
public class Member {

    @Id
    private String email;

    private String pw;
    private String nickname;
    private boolean social;

    @ElementCollection(fetch = FetchType.EAGER) // 즉시 연결
    @Builder.Default
    private List<MemberRole> memberRolesList = new ArrayList<>();

    // 권한 부여 메서드
    // 특정 회원 1개이상 권한 부여하는 메서드(USER, MANAGER, ADMIN)
    public void addRole(MemberRole memberRole) {
        memberRolesList.add(memberRole);
    }
    // 권한 해제 메서드
    public void clearRole(){
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

}
