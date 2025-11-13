package com.example.mallapi.mall.controller;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.MemberFormDTO;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import com.example.mallapi.mall.exception.member.MemberExceptions;
import com.example.mallapi.mall.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder; // 회원 정보  수정작업 -> 암호화된 비밀번호와  암호화되기전 현재 비밀번호 값일치 여부확인용

    //  같은 도메인에서 요청한 url 인지 식별(rest api, 다른 도메인)
    //String sessionRequestUrl = "";

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

            //  자바스크립트에 처리할 메서지
            // e.getMessage()메서드 사용자가 정의 클래스의 message멤버변수값 추출
            model.addAttribute("errorMessage", e);
            log.info("----- e.toString(): {}", e.toString());
            log.info("----- errorMessage: {}", e.getMessage());

            // 회원 가입에서 중복체크시 memberFormDTO.setEmail()에 값 있는 것으로 판단하여 회원조회(수정)폼 이동을 방지 하기 위해
            // 인위적으로 값을 null로설해서 회원 가입 폼으로 인식 시킴
            if (e.getMessage().equals("DUPLICATE")) memberFormDTO.setEmail(null);
            //  MemberExceptions 커스텀 예외처리 클래스


            return "mall/members/memberForm";
        }

        return "redirect:/";
    }

    // ------------------------------------- //
    // 회원 조회
    // ------------------------------------- //
    // 현재 로그인 사용자와 매니저 및 관리자만 접근할 수 있게 어노테이션 추가
     //@PreAuthorize("#email == authentication.principal.username or hasAnyRole('MANAGER', 'ADMIN')")
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


            //  자바스크립트에 처리할 메서지
            // e.getMessage()메서드 사용자가 정의 클래스의 message멤버변수값 추출
            model.addAttribute("errorMessage", e);
            log.info("----- errorMessage: {}", e.getMessage());
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
             /* 비빌번호 변경 기능 별도 구현 -> 테스트용으로 회원 정보수정에서 비밀번호 확인후  새비밀번호 설정하는 기능 구현
                : 입력비밀번호 확인, 새비밀번호, 새비밀번호 확인

              1. 현재 비빌 번호로 DB에 저장된 비밀번호 동일 여부 체크
                 : 암호화회원 정보  수정작업 -> 암호화된 비밀번호와  암호화되기전 현재 비밀번호 값일치 여부확인용

              */
            // 테스트 구간 시작  :  비밀번호 확인후 새비밀번호 설정하는 기능 구현 필요 없을 경우 주석 처리
            /*
            MemberFormDTO  findMemberFormDTO = memberService.findMember(memberFormDTO.getEmail());
            log.info("-------- 현재비밀번호: {} , 기존비밀번호: {} ", memberFormDTO.getCurrentPw(), findMemberFormDTO.getSavedPw());


            if ( !passwordEncoder.matches(memberFormDTO.getCurrentPw(), findMemberFormDTO.getSavedPw()) ) {
                log.info("--------------------- {}", "현재 비밀번호 불일치");
                throw MemberExceptions.BAD_CREDENTIALS.get(); //  MemberExceptions 커스텀 예외처리 클래스
                //throw new IllegalStateException("currentPw");
            }
             log.info("--------------------- {}", "현재 비밀번호 일치");
             */
            // 테스트 구간 끝




            // 서버쪽에서 DTO 데이터 유효성 검사
            // 유효성 검삭결과 1개이상 에러가 있으면 처리

            if (bindingResult.hasErrors()) {
                log.info("-->hasError():" + bindingResult.toString());
                return "mall/members/memberForm";
            }

            //  2. 새비밀번호와 새비밀번호화인 동일 여부 체크
            if (!memberFormDTO.getPw().equals(memberFormDTO.getConfirmPw()))  {
                throw MemberExceptions.INVALID.get(); //  MemberExceptions 커스텀 예외처리 클래스
                //throw new IllegalStateException("confirmPw");
            }

            // 회원 정보 수정 서비스 구현
            Member savedMember = memberService.updateMember(memberFormDTO); // 수정후
            log.info("=> modifiedMember:" + savedMember);

        } catch (Exception e) {// -> 중복된 이메일 등록시 예외발생 처리

            //  자바스크립트에 처리할 메서지
            // e.getMessage()메서드 사용자가 정의 클래스의 message멤버변수값 추출
            model.addAttribute("errorMessage", e);
            return "mall/members/memberForm";
        }

        return "redirect:/members"; // 회원 목록 List로 수정
    }


    // ------------------------------------- //
    //  회원  탈퇴
    // ------------------------------------- //

    // ------------------------------------- //
    // 회원 목록
    // 중복되는 url이 없도록 하나의 컨트롤러에는 하나의 url을 배정하는 방식
    // "/members" , "/members/{page}  => 전체 URL
    // ------------------------------------- //
    @GetMapping(value={"","/{page}"})
    public String memberManage(
            MemberSearchDTO  memberSearchDTO,
            @PathVariable("page") Optional<Integer> page,
            Model model ) {

        /*  */

        // 회원 페이지 기본 설정
        //PageRequest.of(현재페이지번호, 한페이지에 가져올 데이터 개수)
        Pageable pageable = PageRequest.of(page.orElse(0), 2 );// page객체가 널이면 0으로 설정

        // 회원 목록 요청 서비스(검색)
        Map<String, Object> membersResult  = memberService.getAdminMemberPage(memberSearchDTO, pageable);
        log.info("-----> searchDTO, members");
        log.info("search:"+memberSearchDTO);
        log.info("result members: "+membersResult.toString());

        Page<Member> members = (Page<Member>) membersResult.get("members");
        List<MemberFormDTO> memberContent = ( List<MemberFormDTO>)membersResult.get("memberContent");


        // 페이징 처리 속성와 페이징에 표시될 데이터(entity->dto(로그인성공시 데이터추출)->FormDTO 분리 처리
        model.addAttribute("memberContent", memberContent); // 페이징에 보여질 데이터
        model.addAttribute("members", members);   // 페이징 관련 정보
        model.addAttribute("memberSearchDTO", memberSearchDTO);
        model.addAttribute("maxPage", 5); // 페이지 블럭단위(1화면 5페이지)



        return "mall/members/memberMng";
    }




    // ----------------------------------- //
    // 로그인, 로그아웃 처리
    // ----------------------------------- //
    // 1. 로그인
    @GetMapping(value="/login")
    public String loginMember(String error, String logout, HttpServletRequest request){
        log.info("=>get mapping login");

        //  같은 도메인에서 요청한 url 인지 식별(rest api, 다른 도메인)
        //StringBuffer requestURL = request.getRequestURL();
        //sessionRequestUrl =  requestURL.toString();



        return "mall/members/loginForm";
    }
    // 2. 로그아웃
    @GetMapping(value="/login/error")
    public String logoutError(Model model){
        log.info("=>login error");

        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호 확인해주세요");
        //model.addAttribute("requestUrl", sessionRequestUrl);
        return "mall/members/loginForm";
    }

}


/*
요청 URL 추출  Sample예제

1. 객체 주입 및 메서드 사용

import jakarta.servlet.http.HttpServletRequest; // 또는 javax.servlet.http.HttpServletRequest

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    @GetMapping("/api/test")
    public String getUrlInfo(HttpServletRequest request) {
        // 1. 전체 URL (프로토콜, 호스트, 포트, 경로 포함, 쿼리 파라미터 제외)
        StringBuffer requestURL = request.getRequestURL();

        // 2. URI (경로 부분, 컨텍스트 경로 포함, 쿼리 파라미터 제외)
        String requestURI = request.getRequestURI();

        // 3. 쿼리 스트링 (Key=Value&Key=Value)
        String queryString = request.getQueryString();

        // 4. 요청 메서드 (GET, POST, PUT 등)
        String method = request.getMethod();

        // 5. 서버명 (Host)
        String serverName = request.getServerName();

        // 6. 서버 포트
        int serverPort = request.getServerPort();

        // 전체 URL + 쿼리 스트링 (직접 조합)
        String fullUrl = requestURL.toString() + (queryString != null ? "?" + queryString : "");

        System.out.println("Full URL: " + fullUrl);
        System.out.println("URI: " + requestURI);

        return "URL 정보를 콘솔에 출력했습니다.";
    }
}

2. UriComponentsBuilder 사용

URL을 생성하거나 현재 요청 기반으로 URL을 구성할 때 유용한 Spring 클래스입니다. 정적 메서드 fromCurrentRequest() 등을 사용해 현재 요청의 정보

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
public class AnotherUrlController {

    @GetMapping("/api/another-test")
    public String getUriComponentsInfo() {
        // 현재 요청에 기반한 URI 정보를 가져옵니다.
        URI currentUri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        System.out.println("Current URI: " + currentUri.toString());
        System.out.println("Path: " + currentUri.getPath());

        return "UriComponentsBuilder 정보 출력 완료.";
    }
}


 */
