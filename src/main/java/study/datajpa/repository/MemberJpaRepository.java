package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        //JPQL 사용 (객체를 대상으로 하는 쿼리)
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public Optional<Member> findById(Long id){ //null일 수 Member일 수 있음
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }
    public long count(){ //count long 반환
        return em.createQuery("select count(m) from Member m",Long.class)
                .getSingleResult();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }


}
