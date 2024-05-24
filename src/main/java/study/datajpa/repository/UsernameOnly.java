package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    //@Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();
    //username + age+ team이름을 합쳐서 넣어줌 스프링의 SpEL (결국 openProjections으로 데이터를 다 가져옴)
}
