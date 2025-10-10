package com.example.mallapi.mall.controller;

import com.example.mallapi.todo.dto.PageRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MallController {
    // ------------------------------------------------------------------------ //
    @GetMapping("")
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
    // ------------------------------------------------------------------------ //

    @GetMapping("/mall/main")
    public String mainpage(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/main";
    }

    // Todo List
    @GetMapping("/mall/list")
    public String list(PageRequestDTO pageReqeustDTO, Model model) {

//        PageResponseDTO<TodoDTO> pageResponseDTO
//                = todoService.getTodoList(pageReqeustDTO);
//
//        model.addAttribute("todoDTOList", pageResponseDTO.getDtoList());
//        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "todo/list"; // resources/templates/todo/list.html
    }

}
