package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //jpa는 디퐅트 생성자가 필요함 protected 만 허용
@ToString(of = {"id", "username", "age"}) //객체를 바로 찍을 때 출력
public class Member {

    //setter 지양
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }

    }

    //연관관계 세팅 메서드
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
