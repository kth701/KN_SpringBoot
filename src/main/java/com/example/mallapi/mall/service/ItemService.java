package com.example.mallapi.mall.service;


import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.ItemImg;
import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.repository.ItemImgRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    // 파일 업로드 실제 경로
        /*
         properties형식인 경우 최상위 설정
        @Value("${itemImgLocation}")
        */

    // yml형식인 경우 전체 경로 설정
    @Value("${com.example.mallapi.upload.itemImgLocation}")
    private String itemImgLocation;

    //--------------------------------------------------------------------------------- //
    // 1. 상품 정보 등록  (상품 기본 정보, 상품이미지 파일)
    //--------------------------------------------------------------------------------- //
    public Long savedItem(
            ItemFormDTO itemFromDTO,
            List<MultipartFile> itemImFiles) throws  Exception{

        // 1.1 상품 정보 등록
        Item item = itemFromDTO.createItem(); // 상품 등록 화면폼에서 넘온 DTO 데이터 -> Item Entity 맵핑
        itemRepository.save(item);

        // 1.2 상품 이미지 파일 개수만큼  등록
        for (int i=0; i<itemImFiles.size(); i++){
            ItemImg itemImg = new ItemImg();

            // 상품 이미지 엔티티와 상품 엔티티 맵핑
            // 상품 이미지 Entity에 상품Entity조인(연결고리)
            itemImg.setItem(item);

            // 첫번째 상품이미지 Entity를 대표이미지로 설정
            if (i==0){
                itemImg.setRepImgYn("Y");
            } else {
                itemImg.setRepImgYn("N");
            }

            // -------------------------------------------------------- //
            // 상품 이미지 정보 저장 서비스
            // -------------------------------------------------------- //
            //  1. 상품이미지 업로드, 업로된 상품이미지 파일이름 재구성,
            //  2. 업로된 이미지 요청시 사용될 URL 구성
            //  3. 상품이미지 Entity에 저장한 후 DB에 반영
            // -------------------------------------------------------- //
            itemImgService.savedItemImg(itemImg, itemImFiles.get(i));
        }

        return item.getId(); // 상품 엔티티 id반환

    }










}