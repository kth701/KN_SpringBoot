package com.example.mallapi.mall.service;

import com.example.mallapi.constant.ItemSellStatus;
import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.ItemImg;
import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.repository.ItemImgRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Log4j2
@SpringBootTest
@Transactional
@TestPropertySource(locations = {"classpath:application-test.yml" })
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws  Exception{
        List<MultipartFile> multipartFilesList = new ArrayList<>();

        for(int i=0; i<3; i++){
            String path ="c:/upload/item";
            String imageName ="image"+i+".jpg";

            log.info("--> test upload path: {}", path);
            log.info("--> test upload imageName: {}",imageName);

            // 가상의  MultipartFile 리스트 객체 생성해서 반환
            MockMultipartFile multipartFile = new MockMultipartFile(
                    path,
                    imageName,
                    "image/jpg",
                    new byte[] {1,2,3,4});

            multipartFilesList.add(multipartFile);
        }
        return multipartFilesList;

    }

    @Test@DisplayName("상품 등록 테스트")
        //@WithMockUser(username="admin", roles = "ADMIN")
    void saveItem() throws Exception{
        ItemFormDTO itemFormDTO = new ItemFormDTO();

        itemFormDTO.setItemNm("테스트 상품");
        itemFormDTO.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDTO.setItemDetail("테스트 상품입니다.");
        itemFormDTO.setPrice(1000);
        itemFormDTO.setStockNumber(10);

        // 상품 이미지 파일 업로드 테스트
        List<MultipartFile> multipartFileList = this.createMultipartFiles();
        Long itemId = itemService.savedItem(itemFormDTO, multipartFileList);

        // 특정 상품에 대한 상품 이미지 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 상품 정보 등록
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 등록 전후 비교하기
        assertEquals(itemFormDTO.getItemNm(),           item.getItemNm());
        assertEquals(itemFormDTO.getItemSellStatus(),   item.getItemSellStatus());
        assertEquals(itemFormDTO.getItemDetail(),       item.getItemDetail());
        assertEquals(itemFormDTO.getPrice(),            item.getPrice());
        assertEquals(itemFormDTO.getStockNumber(),      item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());

    }

}