package com.example.mallapi.mall.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 등록일, 수정일 상속(BaseTimeEnity)받아 등록자, 수정자 갖는 BaseEntity
@EntityListeners(value={AuditingEntityListener.class})
@MappedSuperclass // 공통 맵핑 정보 제공
@Getter
@Setter
public class BaseEntity extends BaseTimeEnity {
    // 엔티티가 생성되어 저장될 자동으로 등록자, 수정자 자동 저장
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;// 등록자

    // 엔티티의 값을 변경할 때 자동으로 수정일, 수정자 저장
    @LastModifiedBy
    private String modifiedBy;// 수정자
}
