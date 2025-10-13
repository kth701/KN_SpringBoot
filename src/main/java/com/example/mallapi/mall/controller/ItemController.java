package com.example.mallapi.mall.controller;


import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.dto.search.ItemSearchDTO;
import com.example.mallapi.mall.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private  final ItemService itemService;

    // ---------------------------------------------- //
    // 상품 등록 ( 상품 등록 폼, 상품 등록 처리 서비스)
    // ---------------------------------------------- //

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

    // ---------------------------------------------- //
    // 2. 상품 조회(상품 기본정보, 상품 이미지 정보)
    // ---------------------------------------------- //
    @GetMapping(value = {"/admin/item/{itemId}" })
    public String itemDtl(@PathVariable("itemId") Long itemId,  Model model){
        try {

            // 상품 기본정보, 상품이미지 정보 조회 서비스 처리 : .getItemDtl(itemId)
            ItemFormDTO itemFormDTO = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDTO", itemFormDTO);

            log.info("=>itemFormDTO:"+itemFormDTO);

        }catch (EntityNotFoundException e){
            // 등록된 자료가 없으면 상품 등록으로 전환
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDTO", new ItemFormDTO());

            // 상품 등록폼 페이지로 리다이렉트
            return "mall/item/itemForm";
        }

        // 상품 등록 폼 페이지로 리다이렉트(상품 등록, 수정 같은 폼 상용)
        return "mall/item/itemForm";
    }

    // 3. 상품 수정 서비스 처리
    @PostMapping(value ="/admin/item/{itemId}")
    public String itemUpdate(
            @Valid ItemFormDTO itemFormDTO,
            BindingResult bindingResult,
            @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
            Model model){

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
            itemService.updateItem(itemFormDTO, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "mall/item/itemForm";
        }
        return "redirect:/";
    }

    // 4. 상품 목록(검색)
    // 중복되는 url이 없도록 하나의 컨트롤러에는 하나의 url을 배정하는 방식
    // "/admin/item" , "/admin/item/{page} => url중복 에러 발생
    @GetMapping(value={"/admin/items","/admin/items/{page}"})
    public String itemManage(
            ItemSearchDTO itemSearchDTO,
            @PathVariable("page") Optional<Integer> page,
            Model model ){

        // 4.1 페이지 기본 설정
        //PageRequest.of(현재페이지번호, 한페이지에 가져올 데이터 개수)
        Pageable pageable = PageRequest.of(page.isPresent()?page.get(): 0, 10 );

        // 4.2 상품 목록 요청 서비스(검색)
        Page<Item> items = itemService.getAdminItemPage(itemSearchDTO, pageable);
        log.info("---- searchDTO, items");
        log.info("search:"+itemSearchDTO);
        log.info("result items: "+items.getContent());

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDTO", itemSearchDTO);
        model.addAttribute("maxPage", 10); // 페이지 블럭단위(1화면 5페이지)

        return "mall/item/itemMng";

    }

    // 5. 상세 페이지
    @GetMapping(value="/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){

        // 상품 id가지고 상품상세정보 서비스 요청: 상품기본정보, 상품이미지 정보
        ItemFormDTO itemFormDTO = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDTO);

        return "mall/item/itemDtl";
    }
}
