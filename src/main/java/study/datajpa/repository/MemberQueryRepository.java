package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    //굳이 커스텀쪽을 사용하지 않더라도, ex) 화면에 fix한 복잡한 쿼리를 작성해야할떄
    //새로운 리포지터리를 만들어서 사용하는 것을 추천
    List<Member> findALlMembers(){
     return em.createQuery("select m from Member m")
            .getResultList();
    }
}
