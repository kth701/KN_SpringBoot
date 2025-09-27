package com.example.mallapi.mall.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRolesList")
public class Member {

    @Id
    private String email;

    private String pw;
    private String nickname;

    private boolean del;// 회원 탈퇴여부
    private boolean social;// 소셜 로그인 사용 여부


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
    public void changeDel(boolean del) {
        this.del = del;
    }

}
