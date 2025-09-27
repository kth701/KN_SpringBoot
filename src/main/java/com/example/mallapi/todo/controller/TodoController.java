package com.example.mallapi.todo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mallapi.todo.dto.PageRequestDTO;
import com.example.mallapi.todo.dto.PageResponseDTO;
import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.service.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/todos") // URL 경로 구분을 위해 클래스 레벨에 @RequestMapping 추가
@Log4j2
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    /**
     * Thymeleaf 템플릿으로 데이터를 전달하는 샘플 메서드
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return 렌더링할 템플릿의 경로
     */
    @GetMapping("/ex1")
    public String ex1(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "thymeleafEx/ex01";
    }
    @GetMapping("/ex2")
    public String ex2(String username, String password, Model model) {
        log.info("--> 타임리프 매개변수 전달 테스트");
        log.info("username={}", username);
        log.info("password={}", password);

        model.addAttribute("username", username);
        model.addAttribute("password", password);

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "thymeleafEx/mainsub";
    }

    @GetMapping("/main")
    public String mainpage(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/main";
    }
    @GetMapping("/mainsub")
    public String subpage(Model model) {

        // Todo List data 10개 생성
        List<TodoDTO> todoDTOList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TodoDTO todoDTO = new TodoDTO();

            todoDTO.setTno((long) i);
            todoDTO.setTitle("Todo " + i);
            todoDTO.setWriter("Writer " + i);
            todoDTO.setComplete(i % 2 == 0);
            todoDTO.setDueDate(LocalDate.now().plusDays(i));

            todoDTOList.add(todoDTO);
        }

        model.addAttribute("todoDTOList", todoDTOList);


        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "template_webpage/mainsub";
    }



    /*
     * Todo Controller 맵핑
     */

    // Todo Register Form
    @GetMapping("/register")
    public String register(Model model) {
        return "todo/register"; // resources/templates/todo/register.html
    }

    // Login Form
    @GetMapping("/login")
    public String register() {
        return "todo/login"; // resources/templates/todo/register.html
    }
    
    // Todo List
    @GetMapping("/list")
    public String list(PageRequestDTO pageReqeustDTO,Model model) {

        PageResponseDTO<TodoDTO> pageResponseDTO 
                                = todoService.getTodoList(pageReqeustDTO);

        model.addAttribute("todoDTOList", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "todo/list"; // resources/templates/todo/list.html
    }




    /**
     * Todo 상세 조회 메서드
     * @param tno 조회할 Todo 번호 (URL 경로에서 추출)
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return 렌더링할 템플릿의 경로
     */
    @GetMapping("/read/{tno}")
    public String read(@PathVariable("tno") Long tno, Model model) {

        TodoDTO todoDTO = todoService.read(tno);

        model.addAttribute("dto", todoDTO);

        return "todo/read"; // resources/templates/todo/read.html
    }
}
