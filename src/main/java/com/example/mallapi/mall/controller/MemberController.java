package com.example.mallapi.mall.controller;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    // 회원 등록 화면
    @GetMapping(value = {"/new"})
    public String memberRegisterForm(Model model) {
//        model.addAttribute("memberDTO", new MemberDTO(
//                "",
//                "",
//                "",
//                false,
//                false,
//                List.of("USER")
//        ));

        model.addAttribute("memberFormDTO", new MemberFormDTO());
        // 회원가입 폼 포워딩
        return "mall/members/memberForm";
    }

    // ------------------------------------- //
    // 회원 등록 DB처리
    // ------------------------------------- //
    /*
        @PostMapping(value = "/new")
        public String memberRegister(@Valid MemberDTO memberDTO,
                                     BindingResult bindingResult,
                                     Model model
        ) {
     */
    @PostMapping(value = "/new")
    public String memberRegister(@Valid MemberFormDTO memberFormDTO,
                                 BindingResult bindingResult,
                                 Model model
    ) {
        log.info("-->MemberFormDTO controller:" + memberFormDTO);

        // 서버쪽에서 DTO 데이터 유효성 검사
        // 유효성 검삭결과 1개이상 에러가 있으면 처리

        if (bindingResult.hasErrors()) {
            log.info("-->hasError():" + bindingResult.toString());
            return "mall/members/memberForm";
        }

        try {
            /* MemberService에서 인터페이스로 구현
            MemberDTO memberDTO = new MemberDTO(
                    memberFormDTO.getEmail(),
                    memberFormDTO.getPw(),
                    memberFormDTO.getNickname(),
                   false,
                    false,
                    List.of("USER")
            );

            // dto -> entity -> email중복 체크 -> save
            //Member savedMember = memberService.saveMember(memberDTO); // 수정전
             */

            Member savedMember = memberService.saveMember(memberFormDTO); // 수정후
            log.info("=> savedMember:" + savedMember);

        } catch (Exception e) {// -> 중복된 이메일 등록시 예외발생 처리
            // -> 자바스크립트에 처리할 메서지
            model.addAttribute("errorMessage", e.getMessage());
            return "mall/members/memberForm";
        }

        return "redirect:/";
    }

    // ------------------------------------- //
    // 회원 조회
    // ------------------------------------- //
    @GetMapping(value={"/find/{email}"})
    public String findMember(@PathVariable("email") String email, Model model) {
        try {
            MemberFormDTO memberFormDTO = memberService.findMember(email);
            model.addAttribute("memberFormDTO", memberFormDTO);

            log.info("------ 회원수정 memberFormDTO: {}", memberFormDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "mall/members/memberForm";
    }

    // ------------------------------------- //
    // 회원 수정
    // ------------------------------------- //
    @PostMapping(value={"/modify/{email}"})
    public String updateMember(@PathVariable("email") String email,
                                                   @Valid MemberFormDTO memberFormDTO,
                                                   BindingResult bindingResult, Model model){

        log.info("-->MemberFormModifyDTO controller:" + memberFormDTO);

        // 서버쪽에서 DTO 데이터 유효성 검사
        // 유효성 검삭결과 1개이상 에러가 있으면 처리

        if (bindingResult.hasErrors()) {
            log.info("-->hasError():" + bindingResult.toString());
            return "mall/members/memberForm";
        }
        try {

            Member savedMember = memberService.updateMember(memberFormDTO); // 수정후
            log.info("=> modifiedMember:" + savedMember);

        } catch (Exception e) {// -> 중복된 이메일 등록시 예외발생 처리
            // -> 자바스크립트에 처리할 메서지
            model.addAttribute("errorMessage", e.getMessage());
            return "mall/members/memberForm";
        }


        return null;
    }


    // ------------------------------------- //
    //  회원  탈퇴
    // ------------------------------------- //

    // ------------------------------------- //
    // 회원 목록
    // ------------------------------------- //





    // ----------------------------------- //
    // 로그인, 로그아웃 처리
    // ----------------------------------- //
    // 1. 로그인
    @GetMapping(value="/login")
    public String loginMember(String error, String logout){
        log.info("=>get mapping login");

        return "mall/members/loginForm";
    }
    // 2. 로그아웃
    @GetMapping(value="/login/error")
    public String logoutError(Model model){
        log.info("=>login error");

        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호 확인해주세요");
        return "mall/members/loginForm";
    }

}
