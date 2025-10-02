package com.example.mallapi.mall.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 등록일, 수정일 갖는 BaseTimeEntity
@EntityListeners(value={AuditingEntityListener.class})
@MappedSuperclass // 공통 맵핑 정보 제공
@Getter@Setter
public abstract class BaseTimeEnity {
    //엔티티가 생성되어 저장할 때 시간을 자동으로 저장
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regTime;

    //엔티티의 값을 변경할 때 시간을 자동으로 저장
    @LastModifiedDate
    private LocalDateTime updateTime;
}
