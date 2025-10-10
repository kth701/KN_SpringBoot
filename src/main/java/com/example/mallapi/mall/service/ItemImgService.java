package com.example.mallapi.mall.service;

import com.example.mallapi.mall.domain.ItemImg;
import com.example.mallapi.mall.repository.ItemImgRepository;
import com.example.mallapi.mall.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ItemImgService {
    private final ItemRepository itemRepository; // 상품 정보
    private final ItemImgRepository itemImgRepository;// 상품 이미지
    private final FileService fileService; // 파일 업로드 서비스



    // application.properties에서 설정해 놓은 파일 업로드 경로 읽어 오기
    @Value("${com.example.mallapi.upload.itemImgLocation}")
    private String itemImgLocation;


    // 1. 상품 이미지 정보 등록 서비스
    public void savedItemImg(
            ItemImg itemImg,
            MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename();  // input file태그에서 적요된 첨부파일 정보 가져오기
        String imgName =""; // 업로드된 파일이름 추출
        String imgUrl = ""; // 클라이언트가 서버에 있는 이미지 요청시 사용되는 url => <img src='/images/item/xx.png' />

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)){ // 상품이미지가 있으면 처리

            // 파일 업로드 기능 서비스 요청
            // 업로된 후 새로만들어진 파일이름.확장자 반환 : 파일이름_난수.확장자 형식으로 구성
            imgName =  fileService.uploadFile(
                    // "c:/upload/item" => application.yml(.properties)에서 설정한 파입 업로드 경로
                    itemImgLocation,
                    // 상품이미지 파일이름(첨부파일)=> 경로+파일명.확장자인 경우 파일명.확장자만 가져옴.
                    oriImgName,
                    // 파일을 파일의 바이트 배열로 업로드
                    itemImgFile.getBytes()
            );

            // ----------------------------------------------------------------- //
            // application.properties에 설정된 uploadPath와
            // WebMvcConfig 에서 설정한 addResourceHandler에서 1:1 맵핑 관계
            // ----------------------------------------------------------------- //
            // View에 보여질 상품이미지 요청 URL => /images/item/상품이미지파일
            // ----------------------------------------------------------------- //

            // "file:///c:/upload" 와 "/images/item/**" 1:1 맵핑
            // "/images/item/파일이름_난수.확장자" 형식으로 구성하여 DB에 첨부파일 url경로 및 파일이름.확장자로 보관됨.
            // <img src="/images/item/파일이름_난수.확장자"> 형식으로 상품이미지 요청시 이미지 url로 사용됨.
            imgUrl = "/images/item/"+imgName;

        } // end if


        // 상품 이미지 정보 저장 :
        // (업로드 되기전 첨부파일 이미지정보, 업로드된 후 이미지 파일명.확장자, 업로드된 후 이미지 파일 요청 URL)
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);

    }

    // 2. 상품 이미지 정보 수정 서비스
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws  Exception{

        if (!itemImgFile.isEmpty()){ // 상품 이미지가 있으면 처리

            // 2.1 기존 상품 이미지 불러오기
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 2.2 기존 상품 이미지 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())){
                // itemImgLocation매개변수 값: "/images/item"로 설정되어 있음
                // "/images/item/파일이름_난수.확장자"
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }

            // 2.3 변경된 상품이미지 업로드
            String oriImgName = itemImgFile.getOriginalFilename();
           // if (oriImgName == null) throw new AssertionError(); // null인경우 예외처리 상품 이미지가 있는 조건에서 처리하기 때문에 생략
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/"+imgName;

            log.info("=>수정전");
            log.info(oriImgName, imgName);

            // 기존에 db정보를 읽어서 entity에 가져온 상태임 => 영속성 상태를 의미함.
            // savedItemImg영속성 상태이므로 데이터를 변경하는 것만으로 벼녕 감지 기능이 동작하여 트랜젹션이 끝날 때 update쿼리가 실행됨.
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);

            log.info("=>수정후");
            log.info(savedItemImg.getOriImgName(),savedItemImg.getImgName());

        }

    }

}
