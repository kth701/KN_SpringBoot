package com.example.mallapi.mall.service;


import com.example.mallapi.mall.domain.Item;
import com.example.mallapi.mall.domain.ItemImg;
import com.example.mallapi.mall.dto.ItemFormDTO;
import com.example.mallapi.mall.dto.ItemImgDTO;
import com.example.mallapi.mall.dto.MainItemDTO;
import com.example.mallapi.mall.dto.search.ItemSearchDTO;
import com.example.mallapi.mall.repository.ItemImgRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
            List<MultipartFile> itemImgFiles) throws  Exception{

        // 1.1 상품 정보 등록
        Item item = itemFromDTO.createItem(); // 상품 등록 화면폼에서 넘온 DTO 데이터 -> Item Entity 맵핑
        itemRepository.save(item);

        // 1.2 상품 이미지 파일 개수만큼  등록
        for (int i=0; i<itemImgFiles.size(); i++){
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
            itemImgService.savedItemImg(itemImg, itemImgFiles.get(i));
        }

        return item.getId(); // 상품 엔티티 id반환

    }


    //--------------------------------------------------------------------------------- //
    // 2. 상품 정보(상품기본정보, 상품이미지 정보) 조회
    //--------------------------------------------------------------------------------- //
    // @Transactional(readOnly = true): 트랜잭션 읽기 전용: JPA가 더티채킹(변경감지)를
    //      수행하지 않도록 설정(성능향상)
    //--------------------------------------------------------------------------------- //
    @Transactional(readOnly = true)
    public ItemFormDTO getItemDtl(Long itemId){

        // 2.1 상품이미지 정보 조회
        //     : 특정 상품에 대한 상품이미지 정보 조회(상품이미지 아이디오름차순): DB -> entity
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 2.2 List에 있는 Entity -> DTO, DTO -> Etity변환하기: ModelMapper 적용
        List<ItemImgDTO> itemImgDTOList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList){
            // entity -> dto
            ItemImgDTO itemImgDTO = ItemImgDTO.of(itemImg);

            // itemImgDTO  -> list구조에 저장
            itemImgDTOList.add(itemImgDTO);
        }

        // 2.3. 상품 기본 정보 조회
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // 2.4 상품 기본 정보 Entity -> 상품 정보인 ItemFormDTO 전환
        ItemFormDTO itemFormDTO = ItemFormDTO.of(item);
        // 2.5 상품 정보 ItemFormDTO에  상품이미지 정보List 저장
        itemFormDTO.setItemImgDTOList(itemImgDTOList);

        // 2.6 상품 정보(상품기본정보, 상품 이미지 정보)담은 DTO반환 -> 화면(View)용를 사용
        return itemFormDTO;
    }

    //--------------------------------------------------------------------------------- //
    // 3. 상품 정보 수정(상품기본정보, 상품이미지 정보)
    //--------------------------------------------------------------------------------- //
    public Long updateItem( ItemFormDTO itemFormDTO, List<MultipartFile> itemImgFiles) throws Exception{

        // 3.1 기존 상품 정보 호출
        Item item = itemRepository.findById(itemFormDTO.getId())
                .orElseThrow(EntityNotFoundException::new);

        // 3.2 수정 폼으로 부터 전달 받은 상품 정보를 entity 전달(dto -> entity)
        item.updateItem(itemFormDTO);

        // 3.3 수정된 상품 이미지 변경
        // 영속성 상태: OnetoOne , OneToMany에서만 적용 : Entity값 변경시 -> 감지해서 -> update쿼리 실행)
        //itemRepository.save(item); // 생략

        List<Long> itemImgIds = itemFormDTO.getItemImgIds();
        for(int i=0;i<itemImgIds.size(); i++){
            log.info("=>상품이미지수정(itemImgIds):"+itemImgIds);
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFiles.get(i));
        }


        return item.getId();
    }

    //--------------------------------------------------------------------------------- //
    // 4. 상품 검색
    //--------------------------------------------------------------------------------- //
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable){

        return itemRepository.getAdminItemPage(itemSearchDTO, pageable);
    }
    // 5. 메인 화면 상품 리스트
    public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDTO, pageable);
    }

}