package com.example.mallapi.mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormDTO {
    @NotEmpty(message ="이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=4, max=16, message="비밀번호는 4자이상, 16자 이하로 입력해주세요")
    private String pw;

    private String currentPw;
    private String confirmPw;
    private String savedPw;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    private boolean social;
    private boolean del;
    private LocalDateTime regTime;

    @Builder.Default
    private List<String> roleNames = new ArrayList<>();
}
