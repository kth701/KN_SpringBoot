package com.example.mallapi.mall.controller;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder; // 회원 정보  수정작업 -> 암호화된 비밀번호와  암호화되기전 현재 비밀번호 값일치 여부확인용

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
            // 예외 에러 처리시
            // 회원등록 view와 회원조회(수정) view 식별하기 위해 등록일 경우 인위적으로 null로 설정
            memberFormDTO.setEmail(null);
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

            // 회원 가입에서 중복체크시 memberFormDTO.setEmail()에 값 있는 것으로 판단하여 회원조회(수정)폼 이동을 방지 하기 위해
            // 인위적으로 값을 null로설해서 회원 가입 폼으로 인식 시킴
            if (e.getMessage().equals("DuplicateMember")) memberFormDTO.setEmail(null);

            return "mall/members/memberForm";
        }

        return "redirect:/";
    }

    // ------------------------------------- //
    // 회원 조회
    // ------------------------------------- //
    @GetMapping(value={"/find/{email}"})
    public String findMember(@PathVariable("email") String email,
                             MemberFormDTO memberFormDTO,    // 회원 정보 유무와 관계 없이 view에서 memberFormDTO객체에 data binding되어 있어 메서드내에 사용할 수 있는 객체로 선언
                             Model model) {

        try {
            memberFormDTO = memberService.findMember(email);
            // 등록된 회원인 경우 회원 정보를 세션  memberFormDTO속성에 저장
            model.addAttribute("memberFormDTO", memberFormDTO);

            log.info("------ 회원 조회 memberFormDTO: {}", memberFormDTO);
        } catch (Exception e) {
            /*
                 memberService.findMember(email)에 예외발생시 처리
                 등록된 회원인 아닌 경우 세션속성에 errorMessage속성값 저장,  memberFormDTO속성에 비어있는 상태로 저장
                 view에 memberFormDTO속성에 data binding
             */

            //  자바스크립트에서  e.getMessage()속성 값에 따라른 error message  처리
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "mall/members/memberForm";
    }

    // ------------------------------------- //
    // 회원 수정
    // ------------------------------------- //
    @PostMapping(value={"/modify"})
    public String updateMember(//@PathVariable("email") String email,
                                                   @Valid MemberFormDTO memberFormDTO,
                                                   BindingResult bindingResult, Model model){

        log.info("-->MemberFormModifyDTO controller:" + memberFormDTO);




        try {
            //  1. 현재 비빌 번호로 DB에 저장된 비밀번호 동일 여부 체크
            // 회원 정보  수정작업 -> 암호화된 비밀번호와  암호화되기전 현재 비밀번호 값일치 여부확인용
            MemberFormDTO  findMemberFormDTO = memberService.findMember(memberFormDTO.getEmail());
            log.info("-------- 현재비밀번호: {} , 기존비밀번호: {} ", memberFormDTO.getCurrentPw(), findMemberFormDTO.getSavedPw());
            if ( !passwordEncoder.matches(memberFormDTO.getCurrentPw(), findMemberFormDTO.getSavedPw()) ) {
                log.info("--------------------- {}", "현재 비밀번호 불일치");
                throw new IllegalStateException("currentPw");
            }

            log.info("--------------------- {}", "현재 비밀번호 일치");


            // 서버쪽에서 DTO 데이터 유효성 검사
            // 유효성 검삭결과 1개이상 에러가 있으면 처리

            if (bindingResult.hasErrors()) {
                log.info("-->hasError():" + bindingResult.toString());
                return "mall/members/memberForm";
            }

            //  2. 새비밀번호와 새비밀번호화인 동일 여부 체크
            if (!memberFormDTO.getPw().equals(memberFormDTO.getConfirmPw()))  {
                throw new IllegalStateException("confirmPw");
            }

            // 회원 정보 수정 서비스 구현
            Member savedMember = memberService.updateMember(memberFormDTO); // 수정후
            log.info("=> modifiedMember:" + savedMember);

        } catch (Exception e) {// -> 중복된 이메일 등록시 예외발생 처리
            //  자바스크립트에서  e.getMessage()속성 값에 따라른 error message  처리
            log.info("-------------------------------- {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "mall/members/memberForm";
        }


        return "mall/members/memberForm"; // 회원 목록 List로 수정
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
