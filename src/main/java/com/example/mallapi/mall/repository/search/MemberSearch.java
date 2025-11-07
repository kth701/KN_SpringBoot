package com.example.mallapi.mall.repository.search;

import com.example.mallapi.mall.domain.Member;
import com.example.mallapi.mall.dto.search.MemberSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberSearch {
    Page<Member> searchMembers(MemberSearchDTO memberSearchDTO, Pageable pageable);
}
