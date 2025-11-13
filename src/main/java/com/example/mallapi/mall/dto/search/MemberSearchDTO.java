package com.example.mallapi.mall.dto.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원 정보 목록의 검색 조건을 담는 DTO
 */
@Getter
@Setter
@ToString
public class MemberSearchDTO {

    /**
     * 가입일 기준 검색 타입
     * (예: all, 1d, 1w, 1m, 6m)
     */
    private String searchDateType;

    /**
     * 검색 기준 필드
     * (예: email, nickname)
     */
    private String searchBy;

    /**
     * 검색어
     */
    private String searchQuery = "";

    /**
     * 소셜 로그인 회원 필터링 여부
     */
    private String socialType;

    /**
     * 탈퇴 회원 필터링 여부
     */
    private String  delFlag;


}
