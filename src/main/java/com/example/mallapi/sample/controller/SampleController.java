package com.example.mallapi.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/todos") // URL 경로 구분을 위해 클래스 레벨에 @RequestMapping 추가
@Log4j2
@RequiredArgsConstructor
public class SampleController {

    @GetMapping("/mallmain")
    public String mallmain(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/mallmain";
    }
    @GetMapping("/mallsub")
    public String mallsub(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/mallsub";
    }
    @GetMapping("/malldetail")
    public String malldetail(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/malldetail";
    }
    @GetMapping("/malllogin")
    public String malllogin(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/malllogin";
    }
    @GetMapping("/mallsignup")
    public String mallsignup(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/mallsignup";
    }
}
