package com.example.mallapi.mall.controller;


import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private  final ItemService itemService;

    // 1-1. 상품 등록하기 위한 입력 폼 요청
    @GetMapping(value="/admin/item/new")
    public String itemForm(Model model){
        log.info("--> /admin/item/new");

        // ItemFromDTO객체의 속성을 입력 폼의 매개변수로 사용하기위한 설정
        model.addAttribute("itemFormDTO", new ItemFormDTO());
        return "mall/item/itemForm";
    }

    // 1-2. 상품 기본정보, 상품 이미지 정보 : Entity -> DTO, upload작업 처리
    @PostMapping(value="/admin/item/new")
    public String itemNew(
            @Valid ItemFormDTO itemFormDTO,
            BindingResult bindingResult,
            Model model,
            // <inpt type='file' name='itemImgFile'"/> 배열구조(List)인 경우 List<MultipartFile>
            @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList
    ){


        if (bindingResult.hasErrors()){
            // 유효성 검사후 이상 있으면 상품 등록 화면으로 전환
            return "mall/item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null){
            // 대표이미지(첫번째 이미지)가 없을 경우 에러 메시지 처리 :
            // 상품등록폼에 객체 공유 : javascript alert(메시지 처리)
            model.addAttribute("errorMessage", "첫번째 상품(대표)이미지는 필수 입력 값입니다.");
            // 상품 대표이미지가 없으면 상품 등록 화면으로 전환
            return "mall/item/itemForm";
        }

        try {
            itemService.savedItem(itemFormDTO, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "mall/item/itemForm";
        }
        return "redirect:/";
    }
}
