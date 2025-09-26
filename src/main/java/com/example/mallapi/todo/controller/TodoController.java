package com.example.mallapi.todo.controller;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.todo.dto.TodoDTO;
import com.example.mallapi.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/main")
    public String mainpage(Model model) {
        model.addAttribute("message", "TodoController에서 보낸 메시지입니다.");

        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        return "thymeleafEx/main";
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
        return "thymeleafEx/mainsub";
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
