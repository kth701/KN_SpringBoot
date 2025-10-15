package com.example.mallapi.mall.controller;

import com.example.mallapi.mall.dto.MainItemDTO;
import com.example.mallapi.mall.dto.search.ItemSearchDTO;
import com.example.mallapi.mall.service.ItemService;
import com.example.mallapi.todo.dto.PageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class MallController {

    private  final ItemService itemService;

    // ------------------------------------------------------------------------ //
    @GetMapping("")
    public String mallmain(ItemSearchDTO itemSearchDTO,
            Optional <Integer> page,
            Model model){
        // 페이징 설정
        Pageable pageable = PageRequest.of(page.isPresent()?page.get(): 0, 3);
        Page<MainItemDTO> items = itemService.getMainItemPage(itemSearchDTO, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDTO", itemSearchDTO);
        model.addAttribute("maxPage",3); // 페이지블럭(화면에 보여질 페이지 범위)

        log.info("=>index:"+items.getContent());



        //model객체 유지한체로 thymeleafEx/ex01.html로 이동
        //return "template_webpage/mallmain";
        return "template_webpage/mainbase";
    }





    // mall UI Test

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
