package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends JpaBaseEntity{ //JpaBaseEntity 공통으로 사용가능

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") //포린키가 없는 쪽에 mappedBy
    List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
