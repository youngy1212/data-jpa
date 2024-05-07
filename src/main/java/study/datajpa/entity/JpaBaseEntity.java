package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //진짜 상속관계가 아니고, 속성을 내려서 같이 쓸게해주는 것
public class JpaBaseEntity {

    @Column(updatable = false) //변경금지
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist(){ //이번트 전 발생
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }

    @PreUpdate
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }

}
