package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id //@GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createDate;

    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() { //새거인지 아닌지에 대한 로직을 내가 짜야함
        return createDate == null;
    }

    public Item(String id) {
        this.id = id;
    }

}
