package com.example.mallapi.mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;


/*
    주의: 입력폼 MemberFormDTO와 MemberDTO구분됨
    MemberFormDTO: 클라이언트로 부터 회원 가입 정보 전달용
    MemberDTO  Security로그인 처리를 위한 User객체 상속받아 처리하는 DTO
 */
@Getter
@Setter
@ToString
public class MemberFormDTO {
    @NotEmpty(message ="이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=4, max=16, message="비밀번호는 4자이상, 16자 이하로 입력해주세요")
    private String pw;// 새 비밀번호

    private String currentPw; // 현재 비밀번호 (수정작업: 입력시 기존 비밀번호 동일 여부확인)
    private String confirmPw; // 새비밀번호확인(새 비밀번호 동일여부 확인0
    private String savedPw; // 기존 비빌번호


    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;
    private boolean social;
}
