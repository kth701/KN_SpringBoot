package com.example.mallapi.mall.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="item_img")
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@Builder
public class ItemImg extends BaseEntity{
    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 상품 이미지 식별ID(PK)

    private String imgName; // 상품 이미지 파일명

    private String oriImgName; // 상품 원본 이미지 파일명

    private String imgUrl; // 이미지 조회 경로

    private String repImgYn; // 대표 이미지 여부

//    private LocalDateTime regTime; // 등록 시간
//    private LocalDateTime updateTime; // 수정시간

    // 하나의 상품은 여러 상품 이미지를 가질 수 있는 관계 맵핑
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩(상품 이미지는 필요한 시점에 호출)
    @JoinColumn(name="item_id")
    private Item item;

    // 상품 이미지 엔티티 정보 수정하는 메서드(상품 원본 이미지 파일명, 상품 이미지 파일명, 상품 이미지 조회 경로)
    public void updateItemImg(String oriImgName, String imgName, String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }



}
