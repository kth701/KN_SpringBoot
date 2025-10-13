package com.example.mallapi.mall.dto.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* 서버페이지(thymeleaf) -> 페이징 처리위한 PageRequestDTO */

@Builder
@Data
@AllArgsConstructor@NoArgsConstructor
public class PageRequestDTO {

    // 현재 기본 페이지 설정
    @Builder.Default
    private int page = 1;
    // 1페이지에 읽어올 자료(data)개수: 1page=> 10개
    @Builder.Default
    private int size = 10;
    // 검색 타입(제목, 내용, 작성자): t,c,w,tc, tw, twc
    private String type;
    // 검색 키워드
    private String keyword;


    // 검색 타입구분 처리하는 메서드
    public String[] getTypes(){
        if (type==null || type.isEmpty()) return null;

        // 문자열을 문자단위 잘라서 배열에 저장:
        // ["t"],["c"],["w"],["tc"],["tw"],["tcw"]
        return type.split("");
    }

    // 페이징 초기값 설정 : 가변인자=> (...매개변수) => 매개변수 => 배열구조
    public Pageable getPageable(String ...props){

        //  Pageable에서 첫번째페이지 번호 : 0부터 설정, 1페이지를 0페이지 맵핑 형태
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    // 요청할 url에 전달할  페이징 및 검색 관련 매개변수 설정
    // ~~~?page=1&size=10&type=twc&keyword=URLEncode.encode('홍길동').....
    private String link;
    public String getLink(){
        if (link == null){
            StringBuilder builder = new StringBuilder();

            builder.append("page="+this.page);
            builder.append("&size="+this.size);

            // 검색 유형 유무에 따른 내용 처리
            if (type != null && type.length()>0)
                builder.append("&type="+type);

            // 검색키워드에 따른 내용 처리
            if (keyword != null){
                try {
                    builder.append("&keyword="+ URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }// end if

            // StringBuilder -> String 전환
            link = builder.toString();
            // link = page=1&size=10&type=twc&keyword=URLEncode.encode('홍길동')
        }
        return link;
    }

}
